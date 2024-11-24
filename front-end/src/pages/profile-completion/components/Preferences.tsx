import React, { useState } from 'react';
import InputSelect5 from '@/components/ui/InputSelect5';
import OneHandleSlider from '@/components/ui/OneHandleSlider';
import { FaArrowLeft } from 'react-icons/fa';

interface PreferencesProps {
  onPrevious: () => void;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  formData: any;
}

const Preferences: React.FC<PreferencesProps> = ({
  onPrevious,
  onChange,
  formData,
}) => {
  const [gender, setGender] = useState('');
  const [distance, setDistance] = useState('');

  const submitForm = (e: React.FormEvent) => {
    e.preventDefault();
    const user = {
      gender,
      distance,
    };
    console.log(user);
  };

  return (
    <form
      onSubmit={submitForm}
      className="w-full max-w-md rounded-lg bg-accent-200 p-6 shadow-md"
    >
      <h2 className="border-b-2 border-accent text-center text-2xl font-bold text-text">
        Preferences
      </h2>
      <div className="flex flex-col gap-4">
        <InputSelect5
          label="I'm interested in"
          options={['Men', 'Women', 'Everyone']}
          onChange={setGender}
        />
        <OneHandleSlider
          name="Distance"
          min="50"
          max="300"
          step="10"
          value="300"
          label="Maximum distance"
          onChange={setDistance}
        />
      </div>
      <div className="flex flex-row-reverse gap-2">
        <button
          className="mb-3 flex w-full items-center justify-center gap-2 self-start rounded-md bg-primary px-5 py-2 font-semibold tracking-wide text-text transition-colors hover:bg-primary-200 hover:text-text"
          type="submit"
          aria-label="Submit form."
        >
          Finish
        </button>
        <button onClick={onPrevious} className="mb-3 flex w-full items-center justify-center gap-2 self-start rounded-md bg-primary px-5 py-2 font-semibold tracking-wide text-text transition-colors hover:bg-primary-200 hover:text-text">
          <FaArrowLeft />
          Back
        </button>
      </div>
    </form>
  );
};

export default Preferences;
