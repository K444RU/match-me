const FormResponse = ({
  title = 'Something went wrong...',
  subtitle = 'Please contact support.',
}: {
  title?: string;
  subtitle?: string;
}) => {
  return (
    <div className="flex w-full flex-col items-center justify-center border border-red-800 bg-red-200 p-2">
      <h2 className="text-base font-semibold md:text-sm">{title}</h2>
      <p className="text-sm text-text-500 md:text-xs">{subtitle}</p>
    </div>
  );
};

export default FormResponse;
