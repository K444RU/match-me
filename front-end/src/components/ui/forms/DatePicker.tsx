import React, { useState, useRef, useEffect } from 'react';
import { DayPicker } from 'react-day-picker';
import { LuCalendar } from 'react-icons/lu';
import { format } from 'date-fns';
import 'react-day-picker/style.css';

interface DatePickerProps {
    label?: string;
    selectedDate: Date | undefined;
    onDateChange: (dateString: string) => void;
}

const DatePicker: React.FC<DatePickerProps> = ({ label, selectedDate, onDateChange }) => {
    const [showDatePicker, setShowDatePicker] = useState(false);
    const [localDate, setLocalDate] = useState<Date | undefined>(selectedDate);

    const datePickerRef = useRef<HTMLDivElement>(null);
    const datePickerBtnRef = useRef<HTMLButtonElement>(null);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (
                datePickerRef.current &&
                !datePickerRef.current.contains(event.target as Node) &&
                !datePickerBtnRef.current?.contains(event.target as Node)
            ) {
                setShowDatePicker(false);
            }
        };

        if (showDatePicker) document.addEventListener('mousedown', handleClickOutside);

        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, [showDatePicker]);

    useEffect(() => {
        if(selectedDate) {
            setLocalDate(selectedDate)
        }
    }, [selectedDate])

    const handleDateChange = (date: Date | undefined) => {
        if (date) {
            setLocalDate(date);
            const utcDate = new Date(date.getTime() - (date.getTimezoneOffset() * 60000));
            onDateChange(utcDate.toISOString().split('T')[0]);
        } else {
            setLocalDate(undefined);
            onDateChange('');
        }
        setShowDatePicker(false);
    };

    return (
        <div>
            {label && <span className="pl-1 font-semibold">{label}</span>}
            <button
                type='button'
                ref={datePickerBtnRef}
                onClick={() => setShowDatePicker(!showDatePicker)}
                className="flex w-full items-center justify-center gap-2 self-start rounded-md px-5 py-2 font-semibold tracking-wide text-text transition-colors bg-primary hover:bg-primary-200 hover:text-text"
            >
                {!localDate ? (
                    <>
                        <LuCalendar /> Pick a date
                    </>
                ) : (
                    <>
                        <LuCalendar /> {format(localDate, 'PPP')}
                    </>
                )}
            </button>
            {showDatePicker && (
                <div
                    ref={datePickerRef}
                    className="absolute z-10 mt-1 rounded-md border border-gray-200 bg-white p-2 shadow-lg"
                >
                    <DayPicker
                        mode="single"
                        selected={localDate}
                        onSelect={handleDateChange}
                        defaultMonth={localDate || new Date(2000, 0)}
                        captionLayout="dropdown"
                        disabled={[{ before: new Date(1900, 0, 1), after: new Date() }]}
                    />
                    <div className="flex justify-between mt-2">
                        <button
                            onClick={() => handleDateChange(undefined)}
                            className="flex w-full items-center justify-center gap-2 self-start rounded-md px-5 py-2 font-semibold tracking-wide text-text transition-colors bg-primary hover:bg-primary-200 hover:text-text"
                        >
                            Clear
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default DatePicker;
