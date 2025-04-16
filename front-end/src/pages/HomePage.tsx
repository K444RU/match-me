import { useRef } from 'react';
import Hero from './sections/Hero';
import HowItWorksSection from './sections/HowItWorksSection';
import JumpToTopButton from '../components/ui/buttons/JumpToTopButton';

const HomePage = () => {
  const howItWorksRef = useRef<HTMLDivElement>(null);

  const scrollToHowItWorks = () => {
    howItWorksRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <div>
      <Hero onLearnMore={scrollToHowItWorks} />
      <HowItWorksSection ref={howItWorksRef} />
      <JumpToTopButton />
    </div>
  );
};

export default HomePage;
