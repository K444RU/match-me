import { useState, useEffect } from 'react';
import axios from 'axios';

interface City {
    name: string;
    latitude: number;
    longitude: number;
}

const API_KEY = import.meta.env.VITE_GEOAPI_KEY;

const useBrowserLocation = () => {
    const [location, setLocation] = useState<City | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {

        if (!navigator.geolocation) {
            setError('Geolocation is not supported.');
            return;
        }

        navigator.geolocation.getCurrentPosition(
            async (position) => {
                const { latitude, longitude } = position.coords;
                try {
                    const response = await axios.get(
                        `https://api.api-ninjas.com/v1/reversegeocoding?lat=${latitude}&lon=${longitude}`,
                        {
                            headers: { 'X-Api-Key': API_KEY },
                        }
                    );
                    const data = response.data;
                    if (data && data.length > 0) {
                        const city = data[0].name;
                        setLocation({ name: city, latitude, longitude });
                    } else {
                        setError('Unable to determine city from location.');
                    }
                } catch (err) {
                    setError('Failed to reverse geocode location.');
                }
            },
            (err) => {
                setError(`Permission denied or location unavailable: ${err.message}`);
            }
        );
    }, []);

    return { location, error };
};

export default useBrowserLocation;