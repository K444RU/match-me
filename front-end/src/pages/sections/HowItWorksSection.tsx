import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { BrainCircuit, ClipboardList, Users } from 'lucide-react';
import { forwardRef } from 'react';

const steps = [
  {
    icon: <ClipboardList size={32} className="text-accent" />,
    title: 'Share Your Preferences',
    description: "Tell us what you're looking for. We focus on the factors that genuinely matter for compatibility.",
  },
  {
    icon: <BrainCircuit size={32} className="text-accent" />,
    title: 'Our Algorithm Analyzes',
    description:
      'Our transparent algorithm gets to work, analyzing compatibility based on your criteria – not on maximizing engagement.',
  },
  {
    icon: <Users size={32} className="text-accent" />,
    title: 'Receive Quality Matches',
    description:
      'Get matched with individuals who align with your preferences, focusing on quality connections over quantity.',
  },
];


const HowItWorksSection = forwardRef<HTMLDivElement>((_, ref) => {
  return (
    <div ref={ref} className='pb-40' data-testid="how-it-works-section">
      <div className="container mx-auto px-4 text-center">
        <h2 className="mb-4 text-3xl font-bold tracking-tight sm:text-4xl">
          Transparent Matching Designed For You
        </h2>
        <p className="mx-auto mb-12 max-w-2xl text-lg leading-8">
          Unlike other platforms, <span className="text-accent">we prioritize genuine compatibility</span>. Our focus is on understanding your needs to find
          the best match, not keeping you hooked.
        </p>
        <div className="grid grid-cols-1 gap-8 md:grid-cols-3">
          {steps.map((step, index) => (
            <Card key={index} className="flex flex-col items-center text-center text-foreground bg-foreground/5 backdrop-blur-lg">
              <CardHeader className="w-full">
                <CardTitle className="mt-4 w-full">
                    <div className="flex items-center gap-1">
                      {step.icon}
                    {step.title}
                    </div>
                </CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-foreground/80">{step.description}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    </div>
  );
});

HowItWorksSection.displayName = 'HowItWorksSection';

export default HowItWorksSection;
