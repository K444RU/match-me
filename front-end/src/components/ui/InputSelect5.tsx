import React, { useState } from 'react'

type InputSelectProps = {
  label?: string;
  options: string[];
  onChange: React.Dispatch<React.SetStateAction<string>>;
  className?: string;
}

const InputSelect = (
  {
    label,
    options,
    onChange,
    className,
  }: InputSelectProps
) => {
  const [selectedOption, setSelectedOption] = useState<string | null>(null);

  const handleOptionClick = (option: string) => {
    setSelectedOption(option);
    onChange(option)
  }


  return (
    <div className={`w-auto text-text ${className ? className : ''}`}>
      <h2 className="pl-1 font-bold">{label}</h2>
      <div className="flex flex-row h-10 bg-primary-50 rounded-md border-2 border-accent cursor-pointer">
      {options.map((option: any) => (
         <div 
          key={option} 
          className={`px-3 py-2 ${selectedOption === option ? 'bg-accent text-white rounded-sm border-accent' : ''}`}
          onClick={() => handleOptionClick(option)}>{option}</div>))}
      </div>
    </div>
  )
}

export default InputSelect