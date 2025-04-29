import MotionSpinner from '@/components/animations/MotionSpinner';
import { Button } from '@/components/ui/button';
import { DualRangeSlider } from '@/components/ui/dual-range-slider';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Slider } from '@/components/ui/slider';
import { useState } from 'react';
import { FaArrowLeft, FaCheck } from 'react-icons/fa';
import { UnifiedFormData } from '../types/types';
import { genders } from '@/assets/genders';

interface PreferencesProps {
  onPrevious: () => void;
  onNext: () => void;
  formData: UnifiedFormData;
  loading: boolean;
  onChange: (name: keyof UnifiedFormData, value: UnifiedFormData[keyof UnifiedFormData]) => void;
}

export default function Preferences({
  onPrevious,
  onNext,
  formData,
  loading,
  onChange,
}: PreferencesProps) {
  const [error, setError] = useState<string | null>(null);

  const handleValidation = () => {
    if (!formData.genderOther) {
      setError('Please select a gender preference.');
      return false;
    }
    if (formData.ageRange[0] > formData.ageRange[1]) {
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
    <form onSubmit={(e) => e.preventDefault()} className="flex flex-col items-center gap-4">
      {error && <div className="text-sm text-red-500">{error}</div>}

      {/* Gender Preference Dropdown */}
      <div className="w-full space-y-2">
        <Label htmlFor="genderOther">Gender Preference</Label>
        <Select
          name="genderOther"
          defaultValue={formData.genderOther || ''}
          onValueChange={(value) => onChange('genderOther', value)}
        >
          <SelectTrigger className="w-full">
            <SelectValue placeholder="Select Gender" />
          </SelectTrigger>
          <SelectContent>
            {genders.map((gender) => (
              <SelectItem key={gender.value} value={gender.value.toString()}>
                {gender.label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      {/* Distance Slider */}
      <div className="w-full space-y-2">
        <Label htmlFor="distance">Distance | {formData.distance} km</Label>
        <Slider
          min={50}
          max={300}
          step={10}
          value={[formData.distance || 50]}
          onValueChange={(value) => onChange('distance', value[0])}
        />
      </div>

      {/* Age Range Slider */}
      <div className="w-full space-y-2">
        <Label>
          Age range | {formData.ageRange[0]} - {formData.ageRange[1]}
        </Label>
        <DualRangeSlider
          min={18}
          max={120}
          step={1}
          value={formData.ageRange}
          onValueChange={(value) => onChange('ageRange', value)}
        />
      </div>

      {/* Probability Tolerance Slider */}
      <div className="w-full space-y-2">
        <Label htmlFor="probabilityTolerance">Probability Tolerance | {formData.probabilityTolerance}</Label>
        <Slider
          min={0.1}
          max={1.0}
          step={0.1}
          value={[formData.probabilityTolerance || 0.5]}
          onValueChange={(value) => onChange('probabilityTolerance', value[0])}
        />
      </div>

      <div className="mt-6 flex w-full justify-between">
        <Button
          type="button"
          onClick={onPrevious}
          className="flex items-center gap-2 rounded-md bg-primary px-5 py-2 font-semibold text-text hover:bg-primary-200"
        >
          <FaArrowLeft /> Back
        </Button>
        <Button
          type="button"
          onClick={handleFinish}
          disabled={loading}
          className={`flex items-center gap-2 rounded-md px-5 py-2 font-semibold text-text ${
            loading ? 'cursor-not-allowed bg-gray-400' : 'bg-primary hover:bg-primary-200'
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
        </Button>
      </div>
    </form>
  );
};
