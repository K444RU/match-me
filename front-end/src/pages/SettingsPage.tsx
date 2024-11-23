import React, { useState } from 'react'
import InputField from '../components/ui/InputField'
import InputSelect5 from '../components/ui/InputSelect5'
import InputScroll from '../components/ui/InputScroll'
import UserPreferences from '../components/ui/UserPreferences'

const SettingsPage = () => {

  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [gender, setGender] = useState('');
  const [age, setAge] = useState('');

  const submitForm = (e: React.FormEvent) => {
    e.preventDefault();

    const user = {
      firstName,
      lastName,
      gender,
      age
    }
    console.log(user)
  }

  return (
    <div className="max-w-[800px] mx-auto pt-24 px-5 h-screen overflow-auto bg-background-200 items-center justify-center">
      <UserPreferences />
      <form onSubmit={submitForm} className="flex flex-col items-center gap-3">
        <h2>First section</h2>
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
        <InputSelect5 
          label="Gender"
          options={["Man", "Woman", "Other"]}
          onChange={setGender}
        />
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
        <button
        className="flex w-full items-center justify-center gap-2 self-start rounded-md bg-primary px-5 py-2 font-semibold tracking-wide text-text transition-colors hover:bg-primary-200 hover:text-text"
        type="submit"
        aria-label="Submit form."
      >
        <span>Login</span>
      </button>
      </form>
    </div>
  )
}

export default SettingsPage