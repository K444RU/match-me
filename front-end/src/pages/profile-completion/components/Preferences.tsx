import React, {useState} from 'react';
import OneHandleSlider from '@/components/ui/forms/OneHandleSlider';
import {FaArrowLeft, FaCheck} from 'react-icons/fa';
import MultiHandleSlider from '@/components/ui/forms/MultiRangeSlider';
import { UnifiedFormData } from '../types/types';
import MotionSpinner from '@/components/animations/MotionSpinner';

interface PreferencesProps {
    onPrevious: () => void;
    onNext: () => void;
    formData: UnifiedFormData;
    loading: boolean;
    onChange: (name: keyof UnifiedFormData, value: any) => void;
    genderOptions: { id: number; name: string }[];
}

const Preferences: React.FC<PreferencesProps> = ({onPrevious, onNext, formData, loading, onChange, genderOptions}) => {
    const [error, setError] = useState<string | null>(null);

    const handleValidation = () => {
        if (!formData.genderOther) {
            setError('Please select a gender preference.');
            return false;
        }
        if (formData.ageMin! > formData.ageMax!) {
            setError('Minimum age cannot be greater than maximum age.');
            return false;
        }
        setError(null);
        return true;
    };

    const handleFinish = () => {
        if (handleValidation()) {
            onNext();
        }
    };

    return (
        <form
            onSubmit={(e) => e.preventDefault()}
            className="w-full max-w-md rounded-lg bg-accent-200 p-6 shadow-md"
        >
            <h2 className="border-b-2 border-accent text-center text-2xl font-bold text-text">
                Preferences
            </h2>
            <div className="flex flex-col gap-4 mt-4">
                {error && <div className="text-red-500 text-sm">{error}</div>}

                {/* Gender Preference Dropdown */}
                <div>
                    <label htmlFor="genderOther" className="mb-1 text-sm font-medium text-gray-700">
                        Gender Preference
                    </label>
                    <select
                        id="genderOther"
                        value={formData.genderOther || ''}
                        onChange={(e) => onChange('genderOther', e.target.value)}
                        className="w-full rounded-md border border-gray-300 p-2"
                        required
                    >
                        <option value="" disabled>
                            Select Gender
                        </option>
                        {genderOptions.map((gender) => (
                            <option key={gender.id} value={gender.id}>
                                {gender.name}
                            </option>
                        ))}
                    </select>
                </div>

                {/* Distance Slider */}
                <div>
                    <OneHandleSlider
                        name="distance"
                        min={10}
                        max={300}
                        step={10}
                        value={formData.distance || 300}
                        label="Maximum distance (km)"
                        onChange={(value) => onChange('distance', Number(value))}
                    />
                </div>

                {/* Age Range Slider */}
                <div>
                    <MultiHandleSlider
                        min={18}
                        max={120}
                        minValue={formData.ageMin || 18}
                        maxValue={formData.ageMax || 120}
                        label="Preferred Age Range"
                        onInput={({minValue, maxValue}) => {
                            onChange('ageMin', minValue);
                            onChange('ageMax', maxValue);
                        }}
                    />
                </div>

                {/* Probability Tolerance Slider */}
                <div>
                    <OneHandleSlider
                        name="probabilityTolerance"
                        min={0}
                        max={1}
                        step={0.05}
                        value={formData.probabilityTolerance || 0.5}
                        label="Probability Tolerance"
                        onChange={(value) => onChange('probabilityTolerance', Number(value))}
                    />
                </div>
            </div>

            <div className="flex justify-between mt-6">
                <button
                    type="button"
                    onClick={onPrevious}
                    className="flex items-center gap-2 rounded-md bg-primary px-5 py-2 font-semibold text-text hover:bg-primary-200"
                >
                    <FaArrowLeft/> Back
                </button>
                <button
                    type="button"
                    onClick={handleFinish}
                    disabled={loading}
                    className={`flex items-center gap-2 rounded-md px-5 py-2 font-semibold text-text ${
                        loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-primary hover:bg-primary-200'
                    }`}
                >
                                    {loading ? (
                    <>
                        Saving <MotionSpinner />
                    </>
                ) : (
                    <>
                        Continue <FaCheck />
                    </>
                )}
                </button>
            </div>
        </form>
    );
};

export default Preferences;
