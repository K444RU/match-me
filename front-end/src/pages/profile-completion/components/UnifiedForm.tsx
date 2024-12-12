import {useEffect, useState} from 'react';
import Attributes from './Attributes';
import Preferences from './Preferences';
import axios from "axios";

interface UnifiedFormData {
    gender: string | null;
    dateOfBirth: string;
    city: string;
    latitude: number | null;
    longitude: number | null;
    genderOther: string | null;
    ageMin: number | null;
    ageMax: number | null;
    distance: number | null;
    probabilityTolerance: number | null;
}

const PayloadFormData = (formData: UnifiedFormData) => ({
    gender: formData.gender,
    birthDate: formData.dateOfBirth,
    city: formData.city,
    latitude: formData.latitude,
    longitude: formData.longitude,
    genderOther: formData.genderOther,
    ageMin: formData.ageMin,
    ageMax: formData.ageMax,
    maxDistance: formData.distance,
    probabilityTolerance: formData.probabilityTolerance,
});

const UnifiedForm = () => {
    const [formData, setFormData] = useState<UnifiedFormData>(() =>
        JSON.parse(localStorage.getItem('profileData') || '{}')
    );
    const [genderOptions, setGenderOptions] = useState<{ id: number; name: string }[]>([]);
    const [step, setStep] = useState(0);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        localStorage.setItem('profileData', JSON.stringify(formData));
    }, [formData]);

    useEffect(() => {
        const fetchGenders = () => {
            axios
                .get('/api/genders')
                .then(response => setGenderOptions(response.data))
                .catch(err => console.log('Failed to load gender options: ', err));
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
        setFormData((prev) => ({...prev, [name]: value}));
    };

    const handleFinalSubmit = async () => {
        setLoading(true);
        try {
            const payload = PayloadFormData(formData);
            await axios.post('/api/user/complete-registration', payload);
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
