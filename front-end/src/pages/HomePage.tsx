import { useRef } from 'react';
import Hero from './sections/Hero';
import HowItWorksSection from './sections/HowItWorksSection';
import JumpToTopButton from '../components/ui/JumpToTopButton';

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
      <JumpToTopButton />
    </>
  );
};

export default HomePage;
