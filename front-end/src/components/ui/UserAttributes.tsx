import React, { useState} from 'react'
import InputField from './InputField'
import InputSelect5 from './InputSelect5'
import InputScroll from './InputScroll'

const UserAttributes = () => {

  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [gender, setGender] = useState('')
  const [age, setAge] = useState('')
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
    <form onSubmit={submitForm} className="border-b-2 border-accent mb-3">
      <h2 className="font-bold text-text text-xl mb-3 pl-3 border-b-2 border-accent">Your Attributes</h2>
      <div className="grid grid-cols-2 mb-3">
        <InputField className="pr-20"
          type="text"
          name="first_name"
          placeholder="First name"
          value={firstName}
          onChange={setFirstName}
          required={true} 
        />
        <InputField className="pr-20"
          type="text"
          name="last_name"
          placeholder="Last name"
          value={lastName}
          onChange={setLastName}
          required={true} 
        />
        <InputSelect5 
          label="Gender"
          options={["Male", "Female", "Other"]}
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
      </div>
      <button
        className=" mb-3 flex w-full items-center justify-center gap-2 self-start rounded-md bg-primary px-5 py-2 font-semibold tracking-wide text-text transition-colors hover:bg-primary-200 hover:text-text"
        type="submit"
        aria-label="Submit form."
      >
      <span>Set</span>
      </button>
    </form>

  )
}

export default UserAttributes