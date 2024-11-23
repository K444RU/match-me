import React, { useState } from 'react'
import InputField from '../components/ui/InputField'
import InputSelect from '../components/ui/InputSelect'

const SettingsPage = () => {

  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [gender, setGender] = useState('');

  const submitForm = (e: React.FormEvent) => {
    e.preventDefault();

    const user = {
      firstName,
      lastName,
      gender
    }
    console.log(user)
  }

  return (
    <div className="max-w-[800px] mx-auto pt-24 px-5 h-screen overflow-auto bg-background-200 items-center justify-center">
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
        <InputSelect 
          label="Gender"
          options={["Man", "Woman", "Other"]}
          onChange={setGender}
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