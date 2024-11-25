import React, { useState, useRef, useEffect, ElementRef } from 'react';
import InputField from '@/components/ui/forms/InputField';
import InputSelect5 from '@/components/ui/forms/InputSelect5';
import { genders } from '@assets/genders';
import Select from '@/components/ui/forms/Select';
import { FaArrowRight } from 'react-icons/fa';
import { LuCalendar } from 'react-icons/lu';
import { DayPicker } from 'react-day-picker';
import Button from '@/components/ui/buttons/Button';
import 'react-day-picker/style.css';
import { format } from 'date-fns';

interface AttributeProps {
  onNext: () => void;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  formData: any;
}

const Attributes: React.FC<AttributeProps> = ({ onNext }) => {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [gender, setGender] = useState('');
  const [dob, setDob] = useState<Date | undefined>(undefined);
  const [showDatePicker, setShowDatePicker] = useState(false);
  const datePickerRef = useRef<HTMLDivElement>(null);
  const datePickerBtnRef = useRef<ElementRef<'button'>>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        datePickerRef.current &&
        !datePickerRef.current.contains(event.target as Node) &&
        !datePickerBtnRef.current?.contains(event.target as Node)
      ) {
        setShowDatePicker(false);
      }
    };

    if (showDatePicker) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [showDatePicker]);

  const submitForm = (e: React.FormEvent) => {
    e.preventDefault();
    const user = {
      firstName,
      lastName,
      gender,
    };
    console.log(user);
  };

  return (
    <form
      onSubmit={submitForm}
      className="w-full max-w-md rounded-lg bg-accent-200 p-6 shadow-md"
    >
      <h2 className="border-b-2 border-accent text-center text-2xl font-bold text-text">
        Personal Information
      </h2>
      <div className="flex flex-col gap-2 py-4">
        <div className="flex gap-2">
          <InputField
            type="text"
            name="first_name"
            placeholder="First name"
            value={firstName}
            onChange={setFirstName}
            required={true}
          />
          <InputField
            type="text"
            name="last_name"
            placeholder="Last name"
            value={lastName}
            onChange={setLastName}
            required={true}
          />
        </div>
        <div className="flex items-center justify-start gap-2">
          <InputSelect5
            label="Gender"
            options={['Male', 'Female', 'Other']}
            onChange={setGender}
          />
          <div
            className={`w-fit place-self-end transition-opacity duration-150 ${gender !== 'Other' ? 'opacity-50' : ''}`}
          >
            <Select
              disabled={gender !== 'Other'}
              name="genderOther"
              options={[
                <option key="default" value="" disabled selected>
                  Select gender...
                </option>,
                ...genders.map((gender) => (
                  <option key={gender.value} value={gender.value}>
                    {gender.label}
                  </option>
                )),
              ]}
            />
          </div>
        </div>
        <div>
          <div className="flex flex-col justify-between gap-2">
            <span className="pl-1 font-semibold">Date of Birth</span>
            <Button
              ref={datePickerBtnRef}
              onClick={() => {
                setShowDatePicker(!showDatePicker);
              }}
            >
              {!dob ? (
                <>
                  <LuCalendar /> Pick a date
                </>
              ) : (
                // TODO: Format to user locale
                // import { es, ru } from 'date-fns/locale'
                // { locale: ru }
                <>
                  <LuCalendar /> {format(dob, 'PPP')}
                </>
              )}
            </Button>
          </div>
          {showDatePicker && (
            <div
              ref={datePickerRef}
              className="absolute z-10 mt-1 rounded-md border border-gray-200 bg-white p-2 shadow-lg"
            >
              <DayPicker
                mode="single"
                selected={dob}
                onSelect={(date) => {
                  setDob(date);
                }}
                disabled={{ after: new Date() }}
              />
            </div>
          )}
        </div>
      </div>
      <button
        className="flex w-full items-center justify-center gap-2 self-start rounded-md bg-primary px-5 py-2 font-semibold tracking-wide text-text transition-colors hover:bg-primary-200 hover:text-text"
        type="submit"
        aria-label="Continue to next step"
        onClick={onNext}
      >
        Continue <FaArrowRight />
      </button>
    </form>
  );
};

export default Attributes;
