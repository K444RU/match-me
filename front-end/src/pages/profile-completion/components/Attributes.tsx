import React, {useState} from 'react';
import InputField from '@/components/ui/forms/InputField';
import {FaArrowRight} from 'react-icons/fa';
import 'react-day-picker/style.css';
import axios from 'axios';
import DatePicker from "@ui/forms/DatePicker.tsx";

interface UnifiedFormData {
    gender: string | null;
    dateOfBirth: string;
    city: string;
    latitude: number | null;
    longitude: number | null;
}

interface AttributesProps {
    onNext: () => void;
    formData: UnifiedFormData;
    onChange: (name: keyof UnifiedFormData, value: any) => void;
    genderOptions: { id: number; name: string }[];
}

const Attributes: React.FC<AttributesProps> = ({onNext, formData, onChange, genderOptions}) => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [suggestions, setSuggestions] = useState<string[]>([]);

    const handleCityChange = async (value: string) => {
        onChange('city', value);
        setSuggestions([]);
        setLoading(true);

        if (value.trim().length > 2) {
            try {
                const response = await axios.get('https://api.api-ninjas.com/v1/geocoding', {
                    params: {city: value, country: 'Estonia'},
                    headers: {'X-Api-Key': 'WQLYVxg0ufojjO+D2zNRxg==yKgUy9J1Vu7Z7KP1'},
                });

                if (response.data && response.data.length > 0) {
                    const cities = response.data.map((location: { name: string }) => location.name);
                    setSuggestions(cities);
                } else {
                    console.log('No city suggestions found for:', value);
                    setSuggestions([]);
                }
            } catch (err) {
                console.error('Error fetching city suggestions:', err);
                setSuggestions([]);
            } finally {
                setLoading(false);
            }
        } else {
            setLoading(false);
        }
    };

    const selectCity = async (city: string) => {
        onChange('city', city);
        setSuggestions([]);
        setLoading(true);

        try {
            const response = await axios.get('https://api.api-ninjas.com/v1/geocoding', {
                params: {city, country: 'Estonia'},
                headers: {'X-Api-Key': 'WQLYVxg0ufojjO+D2zNRxg==yKgUy9J1Vu7Z7KP1'},
            });

            if (response.data && response.data.length > 0) {
                const {latitude, longitude} = response.data[0];

                if (latitude && longitude) {
                    onChange('latitude', latitude);
                    onChange('longitude', longitude);
                } else {
                    console.error('No valid coordinates found in response:', response.data);
                    setError('Unable to fetch valid coordinates for the selected city.');
                }
            } else {
                console.error('No results returned for city:', city);
                setError('City not found. Please try another city.');
            }
        } catch (err) {
            console.error('Error fetching city coordinates:', err);
            setError('Failed to fetch city coordinates. Please try again later.');
        } finally {
            setLoading(false);
        }
    };

    const validateAndProceed = () => {
        if (!formData.gender || !formData.dateOfBirth || !formData.city) {
            setError('Please fill in all required fields.');
            return;
        }
        setError(null);
        onNext();
    };

    return (
        <form onSubmit={(e) => e.preventDefault()} className="w-full max-w-md rounded-lg bg-accent-200 p-6 shadow-md">
            <h2 className="border-b-2 border-accent text-center text-2xl font-bold text-text">
                Personal Information
            </h2>
            <div className="flex flex-col gap-4 py-4">
                {error && <div className="text-red-500 text-sm">{error}</div>}

                {/* Gender Dropdown */}
                <div>
                    <label className="mb-1 text-sm font-medium text-gray-700" htmlFor="gender">
                        Gender
                    </label>
                    <select
                        id="gender"
                        name="gender"
                        value={formData.gender || ''}
                        onChange={(e) => onChange('gender', e.target.value)}
                        className="w-full rounded-md border border-gray-300 p-2"
                        required
                    >
                        <option value="" disabled>
                            Select Gender
                        </option>
                        {genderOptions.map((gender) => (
                            <option key={gender.id} value={gender.name}>
                                {gender.name}
                            </option>
                        ))}
                    </select>
                </div>

                {/* Birth Date Picker */}
                <label className="mb-1 text-sm font-medium text-gray-700" htmlFor="city">
                    Date of Birth
                </label>
                <DatePicker
                    selectedDate={formData.dateOfBirth ? new Date(formData.dateOfBirth) : undefined}
                    onDateChange={(dateString) => onChange('dateOfBirth', dateString)}
                />

                {/* City Input */}
                <div>
                    <label className="mb-1 text-sm font-medium text-gray-700" htmlFor="city">
                        City
                    </label>
                    <InputField
                        type="text"
                        name="city"
                        placeholder="Enter your city"
                        value={formData.city || ''}
                        onChange={handleCityChange}
                    />
                    {suggestions.length > 0 && (
                        <ul className="bg-white border border-gray-300 rounded-md mt-2">
                            {suggestions.map((city, index) => (
                                <li
                                    key={index}
                                    className="p-2 cursor-pointer hover:bg-gray-200"
                                    onClick={() => selectCity(city)}
                                >
                                    {city}
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            </div>

            <button
                className={`flex w-full items-center justify-center gap-2 self-start rounded-md px-5 py-2 font-semibold tracking-wide text-text transition-colors ${
                    loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-primary hover:bg-primary-200 hover:text-text'
                }`}
                type="button"
                disabled={loading}
                onClick={validateAndProceed}
            >
                {loading ? 'Saving...' : 'Continue'} <FaArrowRight/>
            </button>
        </form>
    );
};

export default Attributes;
