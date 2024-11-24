import { useEffect, useState } from 'react';
import Attributes from './Attributes';
import Preferences from './Preferences';

const UnifiedForm = () => {
  const [formData, setFormData] = useState(() => JSON.parse(localStorage.getItem('profileData') || '{}'));
  const [step, setStep] = useState(0);
  const totalSteps = 2;

  useEffect(() => {
    // Save form data to local storage whenever it changes
    localStorage.setItem('profileData', JSON.stringify(formData));
  }, [formData]);

  const handleNextStep = () => {
    setStep(prev => prev + 1);
  };

  const handlePreviousStep = () => {
    setStep(prev => prev - 1);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev: {[key: string]: any}) => ({ ...prev, [name]: value }));
  };

  return (
    <>
      {step === 0 && <Attributes onNext={handleNextStep} onChange={handleChange} formData={formData} />}
      {step === 1 && <Preferences onPrevious={handlePreviousStep} onChange={handleChange} formData={formData} />}
    </>
  );
};

export default UnifiedForm;