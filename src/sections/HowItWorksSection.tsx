import { forwardRef } from 'react';

const HowItWorksSection = forwardRef<HTMLDivElement>((_, ref) => {
    return (
      <div ref={ref} className="min-h-screen bg-background-50 py-20">
        HowItWorksSection
      </div>
    );
  });

export default HowItWorksSection