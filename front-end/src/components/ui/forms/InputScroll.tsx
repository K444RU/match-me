import React from 'react';

type InputScrollProps = {
  label: string;
  name: string;
  min: string;
  max: string;
  step: string;
  placeholder?: string;
  onChange: React.Dispatch<React.SetStateAction<string>>;
  required?: boolean;
  className?: string;
  //OnChange
  //value??
};

const InputScroll = ({
  label,
  name,
  min,
  max,
  step,
  placeholder,
  onChange,
  required,
  className,
}: InputScrollProps) => {
  return (
    <div className={`mb-3 place-items-start text-text ${className}`}>
      <label className="pl-1 font-semibold" htmlFor={name}>
        {label}
      </label>
      <div className="mt-1 rounded-md border-2 border-accent bg-primary-50 p-1">
        <input
          type="number"
          id={name}
          name={name}
          min={min}
          max={max}
          step={step}
          className="w-full bg-primary-50"
          placeholder={placeholder}
          onChange={(e) => onChange(e.target.value)}
          required={required}
        />
      </div>
    </div>
  );
};

export default InputScroll;
