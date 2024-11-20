import React from "react";

type InputFieldProps = {
  label: string;
  name: string; // [htmlFor, id, name]
  type: string;
  placeholder?: string;
  value: string;
  onChange: React.Dispatch<React.SetStateAction<string>>;
  required?: boolean;
  className?: string;
}

const InputField = (
  { label, name, type, placeholder, value, onChange, required, className}: InputFieldProps) => {
  return (
    <div className={`mb-3 place-items-start ${className}`}>
      <label className="ml-1" htmlFor={`${name}`}>{label}</label>
      <div className="rounded-md border border-primary bg-text-100 p-1">
        <input
          type={type}
          id={name}
          name={name}
          className="bg-text-100 w-full"
          placeholder={placeholder}
          required={required}
          value={value}
          onChange={(e) => onChange(e.target.value)}
        />
      </div>
    </div>
  )
}

export default InputField