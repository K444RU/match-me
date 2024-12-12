import { useEffect, useState } from 'react';
import Attributes from './Attributes';
import Preferences from './Preferences';
import axios from 'axios';
import { UnifiedFormData } from '../types/types';

const PayloadFormData = (formData: UnifiedFormData) => ({
    first_name: formData.firstName,
    last_name: formData.lastName,
    alias: formData.alias,
    gender_self: formData.gender,
    birth_date: formData.dateOfBirth,
    city: formData.city.name,
    latitude: formData.city.latitude,
    longitude: formData.city.longitude,
    gender_other: formData.genderOther,
    age_min: formData.ageMin,
    age_max: formData.ageMax,
    distance: formData.distance,
    probability_tolerance: formData.probabilityTolerance,
});

const UnifiedForm = () => {
    const [formData, setFormData] = useState<UnifiedFormData>(() =>
        JSON.parse(localStorage.getItem('profileData') || '{}')
    );
    const [genderOptions, setGenderOptions] = useState<
        { id: number; name: string }[]
    >([]);
    const [step, setStep] = useState(0);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        localStorage.setItem('profileData', JSON.stringify(formData));
    }, [formData]);

    useEffect(() => {
        const fetchGenders = () => {
            axios
                .get('/api/genders')
                .then((response) => setGenderOptions(response.data))
                .catch((err) =>
                    console.log('Failed to load gender options: ', err)
                );
        };
        fetchGenders();
    }, []);

    const handleNextStep = () => {
        setStep((prev) => prev + 1);
    };

    const handlePreviousStep = () => {
        setStep((prev) => prev - 1);
    };

    const handleChange = (name: keyof UnifiedFormData, value: any) => {
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleFinalSubmit = async () => {
        setLoading(true);
        try {
            const token = localStorage.getItem('authToken');
            const payload = PayloadFormData(formData);
            await axios.patch('/api/user/complete-registration', payload, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            alert('Registration successful!');
        } catch (err) {
            console.error('Error during final submission:', err);
            alert('Failed to submit the form. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            {step === 0 && (
                <Attributes
                    onNext={handleNextStep}
                    onChange={handleChange}
                    formData={formData}
                    genderOptions={genderOptions}
                />
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
