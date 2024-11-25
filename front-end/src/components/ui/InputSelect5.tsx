import React, { useState } from 'react';

type InputSelectProps = {
  label?: string;
  options: string[];
  onChange: React.Dispatch<React.SetStateAction<string>>;
  className?: string;
};

const InputSelect5 = ({
  label,
  options,
  onChange,
  className,
}: InputSelectProps) => {
  const [selectedOption, setSelectedOption] = useState<string | null>(null);

  const handleOptionClick = (option: string) => {
    setSelectedOption(option);
    onChange(option);
  };

  return (
    <div className={`w-fit text-text ${className ? className : ''}`}>
      <h2 className="pl-1 font-semibold">{label}</h2>
      <div className="mt-1 flex h-10 cursor-pointer flex-row rounded-md border-2 border-accent bg-primary-50">
        {options.map((option: any) => (
          <div
            key={option}
            className={`px-3 py-2 ${selectedOption === option ? 'rounded-sm border-accent bg-accent text-white' : ''}`}
            onClick={() => handleOptionClick(option)}
          >
            {option}
          </div>
        ))}
      </div>
    </div>
  );
};

export default InputSelect5;
