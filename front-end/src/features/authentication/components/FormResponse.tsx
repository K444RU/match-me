const FormResponse = ({
  title = 'Something went wrong...',
  subtitle = 'Please contact support.',
  state = 'error',
}: {
  title?: string;
  subtitle?: string;
  state?: 'error' | 'success';
}) => {
  return (
    <div
      className={`${
        state === 'error'
          ? 'border-red-800 bg-red-200'
          : 'border-green-800 bg-green-200'
      } flex w-full flex-col items-center justify-center border p-2`}
    >
      <h2 className="text-base font-semibold md:text-sm">{title}</h2>
      <p className="text-sm text-text-500 md:text-xs">{subtitle}</p>
    </div>
  );
};

export default FormResponse;
