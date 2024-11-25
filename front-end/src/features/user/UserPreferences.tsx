import React, { useState } from 'react';
import InputSelect5 from '../../components/ui/InputSelect5';
import OneHandleSlider from '../../components/ui/OneHandleSlider';

const UserPreferences = () => {
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
    <form onSubmit={submitForm} className="mb-3 border-b-2 border-accent">
      <h2 className="mb-3 border-b-2 border-accent pl-3 text-xl font-bold text-text">
        Your preferences
      </h2>
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
      <button
        className="mb-3 flex w-full items-center justify-center gap-2 self-start rounded-md bg-primary px-5 py-2 font-semibold tracking-wide text-text transition-colors hover:bg-primary-200 hover:text-text"
        type="submit"
        aria-label="Submit form."
      >
        <span>Set</span>
      </button>
    </form>
  );
};

export default UserPreferences;
