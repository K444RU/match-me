type InputFieldProps = {
  // label: string;
  name: string; // [htmlFor, id, name]
  type: string;
  placeholder?: string;
  value: string;
  onChange: (value: string) => void;
  required?: boolean;
  className?: string;
  onFocus?: () => void;
  onBlur?: () => void;
  onKeyDown?: (e: React.KeyboardEvent<HTMLInputElement>) => void;
};

const InputField = (
  // { label, name, type, placeholder, value, onChange, required, className}: InputFieldProps) => {
  {
    name,
    type,
    placeholder,
    value,
    onChange,
    required,
    className,
    onFocus,
    onBlur,
    onKeyDown,
  }: InputFieldProps
) => {
  return (
    <div className={className ? className : 'w-full'}>
      {/* <label className="ml-1" htmlFor={`${name}`}>{label}</label> */}
      {/* file:border-0 file:bg-transparent file:text-sm file:font-medium file:text-foreground*/}
      <input
        type={type}
        id={name}
        name={name}
        className="focus-visible:ring-ring flex h-10 w-full rounded-md border border-accent  px-3 py-2 text-base outline-0 ring-accent-500 ring-offset-accent-200 placeholder:text-text-300 focus-visible:outline-hidden focus-visible:ring-2 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 md:text-sm"
        placeholder={placeholder}
        required={required}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        onFocus={onFocus}
        onBlur={onBlur}
        onKeyDown={onKeyDown}
      />
    </div>
  );
};

export default InputField;
