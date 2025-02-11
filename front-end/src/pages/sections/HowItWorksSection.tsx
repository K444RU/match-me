import { forwardRef } from 'react';

const HowItWorksSection = forwardRef<HTMLDivElement>((_, ref) => {
  return (
    <div ref={ref} className="min-h-screen bg-background-50 py-20" data-testid="how-it-works-section">
      HowItWorksSection
    </div>
  );
});

HowItWorksSection.displayName = 'HowItWorksSection';

export default HowItWorksSection;
