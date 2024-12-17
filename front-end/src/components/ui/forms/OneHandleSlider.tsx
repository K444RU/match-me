import { Skeleton } from '../skeleton';

type OneHandleSliderProps = {
    name: string;
    min: number;
    max: number;
    step: number;
    value: number | null;
    onChange: React.Dispatch<React.SetStateAction<number | null>>;
    label?: string;
    className?: string;
    showInputField?: boolean;
};

const OneHandleSlider = ({
    name,
    min,
    max,
    step,
    value,
    onChange,
    label,
    className,
    showInputField = true,
}: OneHandleSliderProps) => {

    const handleSliderMovement = (value: number) => {
        onChange(value);
    };

    return (
        <div className={`mb-3 w-full text-text ${className ? className : ''}`}>
            <h2 className="pl-1 font-semibold">{label}</h2>
            <div className="flex items-center gap-4">
                {value !== null ? (
                    <input
                        className="h-4 w-1/2 cursor-pointer appearance-none rounded-3xl border border-accent bg-primary-50"
                        type="range"
                        id={name}
                        name={name}
                        min={min}
                        max={max}
                        value={value}
                        step={step}
                        onChange={(e) =>
                            handleSliderMovement(Number(e.target.value))
                        }
                    />
                ) : (
                    <div className="w-1/2">
                        <Skeleton className="h-4 w-full rounded-3xl border border-accent" />
                    </div>
                )}
                {showInputField && value !== null && (
                    <div className="flex flex-wrap items-center gap-1">
                        <input
                            className="focus-visible:ring-ring flex h-10 w-16 rounded-md border border-accent bg-primary-50 px-3 py-2 text-base outline-0 ring-accent-500 placeholder:text-text-300 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 md:text-sm"
                            type="text"
                            id="display_value"
                            name="display_value"
                            value={value}
                            onChange={(e) =>
                                handleSliderMovement(Number(e.target.value))
                            }
                        />
                        <p className="text-xl font-semibold text-text">km</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default OneHandleSlider;
