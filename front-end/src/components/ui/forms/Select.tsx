import { ReactElement } from 'react';

interface SelectProps {
  name: string;
  options: ReactElement[];
  disabled?: boolean;
}

const Select = ({ name, options, disabled }: SelectProps) => {
  return (
    <select
      className="focus-visible:ring-ring flex h-10 w-full rounded-md border border-accent bg-primary-50 px-3 py-2 text-base outline-0 ring-accent-500 ring-offset-accent-200 placeholder:text-text-300 focus-visible:outline-hidden focus-visible:ring-2 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 md:text-sm"
      name={name}
      id={name}
      disabled={disabled}
    >
      {options}
    </select>
  );
};

export default Select;
