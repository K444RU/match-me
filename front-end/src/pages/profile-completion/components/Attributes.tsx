import InputField from '@/components/ui/forms/InputField';
import React, {useEffect, useState} from 'react';
import { FaArrowRight } from 'react-icons/fa';
import 'react-day-picker/style.css';
import { HOBBIES } from '@/assets/hobbies';
import MotionSpinner from '@/components/animations/MotionSpinner';
import { Label } from '@/components/ui/label';
import MultipleSelector, { Option } from '@/components/ui/multi-select';
import { useDebounce } from '@/lib/hooks/use-debounce';
import { hobbiesById } from '@/lib/utils/dataConversion';
import DatePicker from '@ui/forms/DatePicker.tsx';
import ProfilePictureUploader from '@ui/forms/ProfilePictureUploader.tsx';
import { City, UnifiedFormData } from '../types/types';
import { CitySuggestions } from './CitySuggestions';
import useBrowserLocation from "@/pages/profile-completion/hooks/useBrowserLocation.ts";

interface AttributesProps {
  onNext: () => void;
  formData: UnifiedFormData;
  onChange: (name: keyof UnifiedFormData, value: UnifiedFormData[keyof UnifiedFormData]) => void;
  genderOptions: { id: number; name: string }[];
}

const Attributes: React.FC<AttributesProps> = ({ onNext, formData, onChange, genderOptions }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [citySearchValue, setCitySearchValue] = useState(formData.city?.name || '');
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [firstName, setFirstName] = useState(formData.firstName || '');
  const [lastName, setLastName] = useState(formData.lastName || '');
  const [alias, setAlias] = useState(formData.alias || '');
  const [hobbies, setHobbies] = useState<Option[] | []>(hobbiesById(formData.hobbies || []));
  const [uploadedImage, setUploadedImage] = useState<string | null>(null);

  const {location: browserLocation, error: locationError } = useBrowserLocation();
  const debouncedCitySearchValue = useDebounce(citySearchValue, 1000);

  useEffect(()=> {
    if(browserLocation && !formData.city) {
        setCitySearchValue(browserLocation.name);
        onChange('city', browserLocation);
    }
    if (locationError) {
        console.log(locationError);
    }
  }, [browserLocation, locationError, formData.city, onChange]);

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
    if (!formData.gender || !formData.dateOfBirth || !formData.city || !firstName || !lastName || !alias) {
      setError('Please fill in all required fields.');
      return;
    }
    onChange('firstName', firstName);
    onChange('lastName', lastName);
    onChange('alias', alias);
    onChange(
      'hobbies',
      hobbies.map((hobby) => parseInt(hobby.value))
    );
    setError(null);
    onNext();
  };

  return (
    <div className="mx-auto h-[78vh] w-full max-w-md rounded-lg bg-accent-200 p-6 shadow-md">
      <form onSubmit={(e) => e.preventDefault()} className="flex h-full flex-col">
        <h2 className="border-b-2 border-accent text-center text-2xl font-bold text-text">Personal Information</h2>
        <div className="no-scrollbar flex h-full flex-col gap-4 overflow-y-auto py-4">
          {error && <div className="text-sm text-red-500">{error}</div>}

          {/* Names */}
          <div className="flex gap-2">
            <div className="flex flex-col">
              <label className="mb-1 text-sm font-medium text-gray-700" htmlFor="city">
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
              <label className="mb-1 text-sm font-medium text-gray-700" htmlFor="city">
                Last name
              </label>
              <InputField type="text" name="lastName" placeholder="Doorstep" value={lastName} onChange={setLastName} />
            </div>
          </div>

          {/* Profile Picture */}
          <div>
            <label className="mb-1 text-sm font-medium text-gray-700">Profile Picture (Optional)</label>
            <ProfilePictureUploader
                currentImage={uploadedImage}
                onUploadSuccess={(base64Image) => {
                  setUploadedImage(base64Image);
                  console.debug('Upload was successful!');
                }}
            />
          </div>

          {/* Alias */}
          <div className="flex flex-col">
            {' '}
            <label className="mb-1 text-sm font-medium text-gray-700" htmlFor="city">
              Alias
            </label>
            <InputField type="text" name="alias" placeholder="Shotgunner404" value={alias} onChange={setAlias} />
          </div>

          {/* Hobbies */}
          <div className="flex flex-col space-y-1.5">
            <Label htmlFor="hobbies">Hobbies</Label>
            <MultipleSelector
              value={hobbies}
              onChange={setHobbies}
              placeholder="Select your hobbies..."
              defaultOptions={HOBBIES}
              groupBy="category"
              hideClearAllButton={true}
              maxSelected={5}
              hidePlaceholderWhenSelected={true}
              className="bg-white"
            />
          </div>
          {/* Gender Dropdown */}
          <div>
            <label className="mb-1 text-sm font-medium text-gray-700" htmlFor="gender">
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
            <label className="mb-1 text-sm font-medium text-gray-700" htmlFor="city">
              Date of Birth
            </label>
            <DatePicker
              selectedDate={formData.dateOfBirth ? new Date(formData.dateOfBirth) : undefined}
              onDateChange={(dateString) => onChange('dateOfBirth', dateString)}
            />
          </div>

          {/* City Input */}
          <div className="relative">
            <label className="mb-1 text-sm font-medium text-gray-700" htmlFor="city">
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
            {loading && (
              <div className="absolute inset-x-0 top-full mt-2 flex justify-center">
                <MotionSpinner />
              </div>
            )}
            <div className={`absolute z-10 ${!showSuggestions ? `hidden` : ``}`}>
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
            loading ? 'cursor-not-allowed bg-gray-400' : 'bg-primary hover:bg-primary-200 hover:text-text'
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
    </div>
  );
};

export default Attributes;
