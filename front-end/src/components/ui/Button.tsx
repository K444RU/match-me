import { forwardRef } from 'react';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  onClick?: () => void;
  children: React.ReactNode;
}
const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  ({ onClick, children }, ref) => {
    return (
      <button
        ref={ref}
        type="button"
        onClick={onClick}
        className="focus-visible:ring-ring inline-flex h-10 items-center justify-center gap-2 whitespace-nowrap rounded-md bg-primary px-4 py-2 text-sm font-semibold tracking-wide text-text ring-offset-background transition-colors duration-300 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0"
      >
        {children}
      </button>
    );
  }
);

export default Button;
