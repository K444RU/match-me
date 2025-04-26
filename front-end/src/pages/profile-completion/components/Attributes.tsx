import {useEffect, useState} from 'react';
import { FaArrowRight } from 'react-icons/fa';
import 'react-day-picker/style.css';
import { HOBBIES } from '@/assets/hobbies';
import MotionSpinner from '@/components/animations/MotionSpinner';
import { Label } from '@/components/ui/label';
import MultipleSelector, { Option } from '@/components/ui/multi-select';
import { useDebounce } from '@/lib/hooks/use-debounce';
import { hobbiesById } from '@/lib/utils/dataConversion';
import DatePicker from '@ui/forms/DatePicker';
import ProfilePictureUploader from '@ui/forms/ProfilePictureUploader';
import { City, UnifiedFormData } from '../types/types';
import CitySuggestions from './CitySuggestions';
import { Input } from '@/components/ui/input';
import { Select, SelectValue, SelectTrigger, SelectContent, SelectItem } from '@/components/ui/select';
import { Button } from '@/components/ui/button';
import useBrowserLocation from "@/pages/profile-completion/hooks/useBrowserLocation.ts";
import { genders } from '@/assets/genders';
import {Textarea} from "@ui/forms/textarea.tsx";

interface AttributesProps {
  onNext: () => void;
  formData: UnifiedFormData;
  onChange: (name: keyof UnifiedFormData, value: UnifiedFormData[keyof UnifiedFormData]) => void;
}

export default function Attributes({ onNext, formData, onChange }: AttributesProps) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [citySearchValue, setCitySearchValue] = useState(formData.city?.name || '');
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [firstName, setFirstName] = useState(formData.firstName || '');
  const [lastName, setLastName] = useState(formData.lastName || '');
  const [alias, setAlias] = useState(formData.alias || '');
  const [aboutMe, setAboutMe] = useState(formData.aboutMe || '');
  const [hobbies, setHobbies] = useState<Option[] | []>(hobbiesById(formData.hobbies || []));
  const [uploadedImage, setUploadedImage] = useState<string | null>(null);

  const {location: browserLocation, error: locationError } = useBrowserLocation();
  const debouncedCitySearchValue = useDebounce(citySearchValue, 400);

  useEffect(()=> {
    if(browserLocation && !formData.city) {
        setCitySearchValue(browserLocation.name);
        onChange('city', browserLocation);
    }
    if (locationError) {
        console.error(locationError);
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
    if (!formData.genderSelf || !formData.dateOfBirth || !formData.city || !firstName || !lastName || !alias) {
      setError('Please fill in all required fields.');
      return;
    }
    onChange('firstName', firstName);
    onChange('lastName', lastName);
    onChange('alias', alias);
    onChange('aboutMe', aboutMe);
    onChange(
      'hobbies',
      hobbies.map((hobby) => parseInt(hobby.value))
    );
    setError(null);
    onNext();
  };

  return (
      <form onSubmit={(e) => e.preventDefault()} className="flex flex-col items-center gap-2">
          {error && <div className="text-sm text-red-500">{error}</div>}

          {/* Names */}
          <div className="flex gap-2">
            <div className="flex flex-col">
              <Label className="mb-1 text-sm font-medium" htmlFor="city">
                First name
              </Label>
              <Input
                type="text"
                name="firstName"
                placeholder="Michael"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
              />
            </div>
            <div className="flex flex-col">
              <Label className="mb-1 text-sm font-medium" htmlFor="city">
                Last name
              </Label>
              <Input
                type="text"
                name="lastName"
                placeholder="Doorstep"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
              />
            </div>
          </div>

          {/* Profile Picture */}
          <div className="w-full space-y-2">
            <Label className="mb-1 text-sm font-medium">Profile Picture (Optional)</Label>
            <ProfilePictureUploader
                currentImage={uploadedImage}
                onUploadSuccess={(base64Image) => {
                  setUploadedImage(base64Image);
                  console.debug('Upload was successful!');
                }}
            />
          </div>

          {/* Alias */}
          <div className="w-full space-y-2">
            <Label htmlFor="alias">
              Alias
            </Label>
            <Input
              type="text"
              name="alias"
              placeholder="Shotgunner404"
              value={alias}
              onChange={(e) => setAlias(e.target.value)}
            />
          </div>

          {/* About Me */}
          <div className="w-full space-y-2">
              <Label htmlFor="aboutMe">About Me (Optional)</Label>
              <Textarea
                  name="aboutMe"
                  placeholder="Tell us a little about yourself"
                  value={aboutMe}
                  onChange={(e) => setAboutMe(e.target.value)}
                  rows={4}
              />
          </div>

          {/* Hobbies */}
          <div className="w-full space-y-2">
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
            />
          </div>
          {/* Gender Dropdown */}
          <div className="w-full space-y-2">
            <Label htmlFor="gender">
              Gender
            </Label>
            <Select
              name="gender"
              defaultValue={formData.genderSelf || ''}
              onValueChange={(value) => onChange('genderSelf', value)}
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

          {/* Birth Date Picker */}
          <div className="w-full space-y-2">
            <Label>
              Date of Birth
            </Label>
            <DatePicker
              selectedDate={formData.dateOfBirth ? new Date(formData.dateOfBirth) : undefined}
              onDateChange={(date) => onChange('dateOfBirth', date.toISOString())}
            />
          </div>

          {/* City Input */}
          <div className="w-full space-y-2">
            <Label>
              City
            </Label>
            <Input
              type="text"
              name="city"
              placeholder="Enter your city"
              value={citySearchValue}
              onChange={(e) => handleCityInputChange(e.target.value)}
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

        <Button
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
        </Button>
      </form>
  );
};
