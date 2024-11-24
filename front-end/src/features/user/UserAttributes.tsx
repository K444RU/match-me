import React, { useState } from 'react';
import InputField from '../../components/ui/InputField';
import InputSelect5 from '../../components/ui/InputSelect5';
import InputScroll from '../../components/ui/InputScroll';
import { genders } from '@/assets/genders';
import Select from '@/components/ui/Select';

const UserAttributes = () => {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [gender, setGender] = useState('Other');
  const [age, setAge] = useState('');
  const submitForm = (e: React.FormEvent) => {
    e.preventDefault();
    const user = {
      firstName,
      lastName,
      gender,
      age,
    };
    console.log(user);
  };

  return (
    <form onSubmit={submitForm} className="max-w-3xl">
      <h2 className="mb-3 border-b-2 border-accent pl-3 text-xl font-bold text-text">
        Your Attributes
      </h2>
      <div className="mb-3 grid grid-cols-2">
        <InputField
          className="pr-20"
          type="text"
          name="first_name"
          placeholder="First name"
          value={firstName}
          onChange={setFirstName}
          required={true}
        />
        <InputField
          className="pr-20"
          type="text"
          name="last_name"
          placeholder="Last name"
          value={lastName}
          onChange={setLastName}
          required={true}
        />
        <div>
          <InputSelect5
            label="Gender"
            options={['Male', 'Female', 'Other']}
            onChange={setGender}
          />
          {gender === 'Other' && (
            <Select name='genderOther' options={genders.map(gender => (
              <option key={gender.value} value={gender.value}>{gender.label}</option>
            ))}/>
          )}
        </div>
        <InputScroll
          label="Age"
          name="user_age"
          min="18"
          max="100"
          step="1"
          placeholder="Age"
          onChange={setAge}
          required={true}
        />
      </div>
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

export default UserAttributes;
