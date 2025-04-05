import { UserParametersRequestDTO } from '@/api/types';
import { useAuth } from '@/features/authentication';
import { genderService } from '@/features/gender';
import { userService } from '@/features/user';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { UnifiedFormData } from '../types/types';
import Attributes from './Attributes';
import Preferences from './Preferences';

const PayloadFormData = (formData: UnifiedFormData): UserParametersRequestDTO => ({
  first_name: formData.firstName,
  last_name: formData.lastName,
  alias: formData.alias,
  hobbies: formData.hobbies || [],
  gender_self: Number(formData.gender),
  birth_date: formData.dateOfBirth,
  city: formData.city.name,
  latitude: formData.city.latitude,
  longitude: formData.city.longitude,
  gender_other: Number(formData.genderOther),
  age_min: formData.ageMin,
  age_max: formData.ageMax,
  distance: formData.distance,
  probability_tolerance: formData.probabilityTolerance,
});

const UnifiedForm = () => {
  const navigate = useNavigate();
  const { fetchCurrentUser } = useAuth();

  const [formData, setFormData] = useState<UnifiedFormData>(() => ({
    ...JSON.parse(localStorage.getItem('profileData') || '{}'),
    probabilityTolerance: 0.5,
    distance: 300,
  }));
  const [genderOptions, setGenderOptions] = useState<{ id: number; name: string }[]>([]);
  const [step, setStep] = useState(0);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    localStorage.setItem('profileData', JSON.stringify(formData));
  }, [formData]);

  useEffect(() => {
    const fetchGenders = async () => {
      await genderService
        .getGenders()
        .then((genders) => setGenderOptions(genders))
        .catch((err) => 
    };
    fetchGenders();
  }, []);

  const handleNextStep = () => {
    setStep((prev) => prev + 1);
  };

  const handlePreviousStep = () => {
    setStep((prev) => prev - 1);
  };

  const handleChange = (name: keyof UnifiedFormData, value: UnifiedFormData[keyof UnifiedFormData]) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFinalSubmit = async () => {
    setLoading(true);
    const payload = PayloadFormData(formData);
    try {
      await userService.updateParameters(payload);
      localStorage.removeItem('profileData');
      await fetchCurrentUser();
      navigate('/chats');
    } catch (err) {
      console.error('Error during final submission:', err);
      if (typeof err === 'string') {
        toast.error(err);
      } else if (err instanceof Error) {
        toast.error(err.message);
      } else {
        toast.error('An unexpected error occurred');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      {step === 0 && (
        <Attributes onNext={handleNextStep} onChange={handleChange} formData={formData} genderOptions={genderOptions} />
      )}
      {step === 1 && (
        <Preferences
          onPrevious={handlePreviousStep}
          onNext={handleFinalSubmit}
          formData={formData}
          loading={loading}
          onChange={handleChange}
          genderOptions={genderOptions}
        />
      )}
    </>
  );
};

export default UnifiedForm;
