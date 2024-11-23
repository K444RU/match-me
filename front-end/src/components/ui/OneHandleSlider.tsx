import React, { useState } from 'react'

type OneHandleSliderProps = {
  name: string;
  min: string;
  max: string;
  step: string;
  value: string;
  onChange: React.Dispatch<React.SetStateAction<string>>;
  label?: string;
  className?: string;
}

const OneHandleSlider = (
  {
    name,
    min,
    max,
    step,
    value,
    onChange,
    label,
    className
  }: OneHandleSliderProps
) => {

  const [displayNumber, setDisplayNumber] = useState<string>(value);

  const handleSliderMovement = (value: string) => {
    setDisplayNumber(value);
    onChange(value);
  }

  return (
    <div className={`w-full mb-3 text-text ${className ? className : ''}`}>
      <h2 className="pl-1 font-semibold">{label}</h2>
      <div className="flex gap-4 items-center">
        <input className="w-1/2 rounded-3xl h-4 border border-accent bg-primary-50 appearance-none cursor-pointer"
        type="range"
        id={name}
        name={name}
        min={min}
        max={max}
        value={displayNumber}
        step={step}
        onChange={(e) => handleSliderMovement(e.target.value)}
        />
        <div className="flex flex-wrap gap-1 items-center">
        <input className="focus-visible:ring-ring flex h-10 w-16 rounded-md border border-accent bg-primary-50 px-3 py-2 text-base outline-0 ring-accent-500 placeholder:text-text-300 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 md:text-sm"
          type="text"
          id="display_value"
          name="display_value"
          value={displayNumber}
          onChange={(e) => handleSliderMovement(e.target.value)}
          />
          <p className="text-text text-xl font-semibold">km</p>
        </div>
      </div>
    </div>
  )
}

export default OneHandleSlider