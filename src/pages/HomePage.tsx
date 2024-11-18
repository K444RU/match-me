import { useRef } from 'react';
import Hero from '../components/Hero';
import HowItWorksSection from '../sections/HowItWorksSection';
import { useState, useEffect } from 'react';
const HomePage = () => {
  const howItWorksRef = useRef<HTMLDivElement | null>(null);

  const scrollToHowItWorks = () => {
    howItWorksRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const [scrollPosition, setScrollPosition] = useState(0);
  const handleScroll = () => {
    const position = window.scrollY;
    setScrollPosition(position);
  };
  
  useEffect(() => {
    window.addEventListener('scroll', handleScroll, { passive: true });
    
    return () => {
      window.removeEventListener('scroll', handleScroll);
    };
  }, []);
  console.log(scrollPosition);
  
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
