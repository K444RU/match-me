import { useEffect, useState } from 'react';
import { geocodingService } from '../utils/geocoding';
import { City } from '../types/types';
import MotionSpinner from '@/components/animations/MotionSpinner';

interface CitySuggestionsProps {
    searchTerm: string;
    onCitySelect: (city: City) => void;
}

export const CitySuggestions: React.FC<CitySuggestionsProps> = ({
    searchTerm,
    onCitySelect,
}) => {
    const [loading, setLoading] = useState(false);
    const [cities, setCities] = useState<City[]>([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchSuggestions = async () => {
            if (searchTerm.trim().length <= 2) {
                setCities([]);
                return;
            }

            setLoading(true);
            try {
                const suggestions =
                    await geocodingService.searchByCity(searchTerm);
                setCities(suggestions);
            } catch (error) {
                setError(
                    error instanceof Error
                        ? error.message
                        : 'Failed to fetch cities'
                );
                setCities([]);
            } finally {
                setLoading(false);
            }
        };

        fetchSuggestions();
    }, [searchTerm]);

    if (loading) {
        return (
            <ul className="mt-1 max-h-60 overflow-y-auto rounded-md border border-gray-300 bg-white shadow-lg">
                <li className="border-b border-gray-100 p-2 text-sm hover:bg-gray-100">
                    <span className="font-medium">
                        <MotionSpinner />
                    </span>
                </li>
            </ul>
        );
    }

    if (error) {
        return (
            <ul className="mt-1 max-h-60 overflow-y-auto rounded-md border border-gray-300 bg-white shadow-lg">
                <li className="border-b border-gray-100 p-2 text-sm hover:bg-gray-100">
                    <span className="font-medium text-red-500">{error}</span>
                </li>
            </ul>
        );
    }

    if (cities.length === 0 && searchTerm.length > 2) {
        return (
            <ul className="mt-1 max-h-60 overflow-y-auto rounded-md border border-gray-300 bg-white shadow-lg">
                <li className="border-b border-gray-100 p-2 text-sm hover:bg-gray-100">
                    <span className="font-medium">No cities found.</span>
                </li>
            </ul>
        );
    }

    return cities.length > 0 ? (
        <ul className="mt-1 max-h-60 overflow-y-auto rounded-md border border-gray-300 bg-white shadow-lg">
            {cities.map((city, index) => (
                <li
                    key={index}
                    className="cursor-pointer border-b border-gray-100 p-2 text-sm hover:bg-gray-100"
                    onClick={() => onCitySelect(city)}
                >
                    <span className="font-medium">{city.name}</span>
                    <span className="ml-2 text-xs text-gray-500">
                        {city.country}
                    </span>
                </li>
            ))}
        </ul>
    ) : null;
};
