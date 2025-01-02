import React, { useState } from 'react';
import InputField from '@/components/ui/forms/InputField';
import { FaArrowRight } from 'react-icons/fa';
import 'react-day-picker/style.css';
import DatePicker from '@ui/forms/DatePicker.tsx';
import { useDebounce } from '@/lib/hooks/useDebounce';
import { CitySuggestions } from './CitySuggestions';
import { City, UnifiedFormData } from '../types/types';
import MotionSpinner from '@/components/animations/MotionSpinner';
import { Hobby } from '@/types/api';
import MultipleSelector from '@/components/ui/multi-select';
import { Label } from '@/components/ui/label';
import { hobbiesById, hobbiesToOptions, optionsToHobbies } from '@/lib/utils/dataConversion';
import { HOBBIES } from '@/assets/hobbies';

interface AttributesProps {
    onNext: () => void;
    formData: UnifiedFormData;
    onChange: (name: keyof UnifiedFormData, value: any) => void;
    genderOptions: { id: number; name: string }[];
}

const Attributes: React.FC<AttributesProps> = ({
    onNext,
    formData,
    onChange,
    genderOptions,
}) => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [citySearchValue, setCitySearchValue] = useState(
        formData.city?.name || ''
    );
    const [showSuggestions, setShowSuggestions] = useState(false);
    const [firstName, setFirstName] = useState(formData.firstName || '');
    const [lastName, setLastName] = useState(formData.lastName || '');
    const [alias, setAlias] = useState(formData.alias || '');
    const [hobbies, setHobbies] = useState<Hobby[] | []>(hobbiesById(formData.hobbies || []));

    const debouncedCitySearchValue = useDebounce(citySearchValue, 1000);

    const handleCitySelect = async (city: City) => {
        setShowSuggestions(false);
        setLoading(true);
        setCitySearchValue(city.name);
        onChange('city', city);
        setLoading(false);
    };

    const handleCityInputChange = (value: string) => {
        setCitySearchValue(value);
        setShowSuggestions(true);
    };

    const validateAndProceed = () => {
        if (
            !formData.gender ||
            !formData.dateOfBirth ||
            !formData.city ||
            !firstName ||
            !lastName ||
            !alias
        ) {
            setError('Please fill in all required fields.');
            return;
        }
        onChange('firstName', firstName);
        onChange('lastName', lastName);
        onChange('alias', alias);
        onChange('hobbies', hobbies.map((hobby) => hobby.id));
        setError(null);
        onNext();
    };

    return (
        <form
            onSubmit={(e) => e.preventDefault()}
            className="w-full max-w-md rounded-lg bg-accent-200 p-6 shadow-md"
        >
            <h2 className="border-b-2 border-accent text-center text-2xl font-bold text-text">
                Personal Information
            </h2>
            <div className="flex flex-col gap-4 py-4">
                {error && <div className="text-sm text-red-500">{error}</div>}

                {/* Names */}
                <div className="flex gap-2">
                    <div className="flex flex-col">
                        <label
                            className="mb-1 text-sm font-medium text-gray-700"
                            htmlFor="city"
                        >
                            First name
                        </label>
                        <InputField
                            type="text"
                            name="firstName"
                            placeholder="Michael"
                            value={firstName}
                            onChange={setFirstName}
                        />
                    </div>
                    <div className="flex flex-col">
                        <label
                            className="mb-1 text-sm font-medium text-gray-700"
                            htmlFor="city"
                        >
                            Last name
                        </label>
                        <InputField
                            type="text"
                            name="lastName"
                            placeholder="Doorstep"
                            value={lastName}
                            onChange={setLastName}
                        />
                    </div>
                </div>

                {/* Alias */}
                <div className="flex flex-col">
                    {' '}
                    <label
                        className="mb-1 text-sm font-medium text-gray-700"
                        htmlFor="city"
                    >
                        Alias
                    </label>
                    <InputField
                        type="text"
                        name="alias"
                        placeholder="Shotgunner404"
                        value={alias}
                        onChange={setAlias}
                    />
                </div>

                {/* Hobbies */}
                <div className="flex flex-col space-y-1.5">
                    <Label htmlFor="hobbies">Hobbies</Label>
                    <MultipleSelector
                    value={hobbiesToOptions(hobbies)}
                    onChange={(value) => setHobbies(optionsToHobbies(value))}
                    placeholder='Select your hobbies...'
                    defaultOptions={hobbiesToOptions(HOBBIES)}
                    groupBy='category'
                    hideClearAllButton={true}
                    maxSelected={5}
                    hidePlaceholderWhenSelected={true}
                    className='bg-white'
                    />
                </div>

                {/* Gender Dropdown */}
                <div>
                    <label
                        className="mb-1 text-sm font-medium text-gray-700"
                        htmlFor="gender"
                    >
                        Gender
                    </label>
                    <select
                        id="gender"
                        name="gender"
                        value={formData.gender || ''}
                        onChange={(e) => onChange('gender', e.target.value)}
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

                {/* Birth Date Picker */}
                <div className="flex flex-col">
                    <label
                        className="mb-1 text-sm font-medium text-gray-700"
                        htmlFor="city"
                    >
                        Date of Birth
                    </label>
                    <DatePicker
                        selectedDate={
                            formData.dateOfBirth
                                ? new Date(formData.dateOfBirth)
                                : undefined
                        }
                        onDateChange={(dateString) =>
                            onChange('dateOfBirth', dateString)
                        }
                    />
                </div>

                {/* City Input */}
                <div>
                    <label
                        className="mb-1 text-sm font-medium text-gray-700"
                        htmlFor="city"
                    >
                        City
                    </label>
                    <InputField
                        type="text"
                        name="city"
                        placeholder="Enter your city"
                        value={citySearchValue}
                        onChange={handleCityInputChange}
                        onFocus={() => setShowSuggestions(true)}
                        onBlur={() => {
                            setTimeout(() => {
                                setShowSuggestions(false);
                            }, 500);
                        }}
                    />
                    <div
                        className={`absolute z-10 ${!showSuggestions ? `hidden` : ``}`}
                    >
                        <CitySuggestions
                            searchTerm={debouncedCitySearchValue}
                            onCitySelect={handleCitySelect}
                            visible={showSuggestions}
                        />
                    </div>
                </div>
            </div>

            <button
                className={`flex w-full items-center justify-center gap-2 self-start rounded-md px-5 py-2 font-semibold tracking-wide text-text transition-colors ${
                    loading
                        ? 'cursor-not-allowed bg-gray-400'
                        : 'bg-primary hover:bg-primary-200 hover:text-text'
                }`}
                type="button"
                disabled={loading}
                onClick={validateAndProceed}
            >
                {loading ? (
                    <>
                        Saving <MotionSpinner />
                    </>
                ) : (
                    <>
                        Continue <FaArrowRight />
                    </>
                )}
            </button>
        </form>
    );
};

export default Attributes;
