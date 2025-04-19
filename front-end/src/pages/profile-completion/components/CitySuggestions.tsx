import { useEffect, useState } from 'react';
import { geocodingService } from '../utils/geocoding';
import { City } from '../types/types';
import MotionSpinner from '@/components/animations/MotionSpinner';
import { Button } from '@/components/ui/button';

interface CitySuggestionsProps {
    searchTerm: string;
    onCitySelect: (city: City) => void;
    visible: boolean;
}

export default function CitySuggestions({
    searchTerm,
    onCitySelect,
    visible
}: CitySuggestionsProps) {
    const [loading, setLoading] = useState(false);
    const [cities, setCities] = useState<City[]>([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchSuggestions = async () => {
            if (!visible || searchTerm.trim().length <= 2) {
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
    }, [searchTerm, visible]);

    if (!visible) return null;

    if (loading) {
        return (
            <ul className="mt-1 max-h-60 overflow-y-auto rounded-md border bg-card shadow-lg">
                <li className="border-b border p-2 text-sm">
                    <span className="font-medium">
                        <MotionSpinner />
                    </span>
                </li>
            </ul>
        );
    }

    if (error) {
        return (
            <ul className="mt-1 max-h-60 overflow-y-auto rounded-md border bg-card shadow-lg">
                <li className="border-b p-2 text-sm">
                    <span className="font-medium text-red-500">{error}</span>
                </li>
            </ul>
        );
    }

    if (cities.length === 0 && searchTerm.length > 2) {
        return (
            <ul className="mt-1 max-h-60 overflow-y-auto rounded-md border bg-card shadow-lg">
                <li className="border-b p-2 text-sm">
                    <span className="font-medium">No cities found.</span>
                </li>
            </ul>
        );
    }

    return cities.length > 0 ? (
        <ul className="mt-1 max-h-60 overflow-y-auto rounded-md border bg-card shadow-lg">
            {cities.map((city, index) => {
                // Round the first item top, last item bottom
                const rounded = index === 0 ? "rounded-t-md" : index === cities.length - 1 ? "rounded-b-md" : "";
                // All but last list item has a border-b
                const border = index === cities.length - 1 ? "" : "border-b";

                return (<li
                    key={index}
                    className={`${border} text-sm`}
                    >
                    <Button
                        variant="ghost"
                        className={`w-full justify-start p-2 rounded-none ${rounded}`}
                        onClick={() => onCitySelect(city)}
                    >
                    <span className="font-medium">{city.name}</span>
                    <span className="ml-2 text-xs text-muted-foreground">
                        {city.country}
                    </span>
                    </Button>
                </li>
            )})}
        </ul>
    ) : null;
};
