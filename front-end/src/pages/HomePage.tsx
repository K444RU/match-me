import { useRef } from 'react';
import Hero from './sections/Hero';
import HowItWorksSection from './sections/HowItWorksSection';
const HomePage = () => {
  const howItWorksRef = useRef<HTMLDivElement>(null);

  const scrollToHowItWorks = () => {
    howItWorksRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <>
      <Hero
        title="Blind"
        subtitle="Ready to date differently?"
        onLearnMore={scrollToHowItWorks}
      />
      <HowItWorksSection ref={howItWorksRef} />
    </>
  );
};

export default HomePage;
