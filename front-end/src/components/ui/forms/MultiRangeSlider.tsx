import MultiRangeSlider, { ChangeResult } from "multi-range-slider-react";

type MultiRangeSliderProps = {
    min: number;
    max: number;
    minValue: number;
    maxValue: number;
    label?: string;
    onInput: (e: ChangeResult) => void;
};

const MultiHandleSlider = ({
                                    min,
                                    max,
                                    minValue,
                                    maxValue,
                                    onInput,
                                    label,
                                }: MultiRangeSliderProps) => {
    return (
        <div className="mb-3 w-full text-text">
            {label && <h2 className="pl-1 font-semibold">{label}</h2>}
            <div className="flex items-center gap-4">
                <div className="w-full">
                    <MultiRangeSlider
                        min={min}
                        max={max}
                        minValue={minValue}
                        maxValue={maxValue}
                        step={1}
                        onInput={onInput}
                        style={{boxShadow: "none"}}
                        ruler={false}
                        barLeftColor="white"
                        barInnerColor="#e67d60"
                        barRightColor="white"
                        thumbLeftColor="#e67d60"
                        thumbRightColor="#e67d60"
                    />
                </div>
                <div className="flex items-center gap-4">
                    <div>
                        <label htmlFor="minValue" className="sr-only">Min Value</label>
                        <input
                            id="minValue"
                            type="number"
                            value={minValue}
                            className="focus-visible:ring-ring flex h-10 w-16 rounded-md border border-accent bg-primary-50 px-3 py-2 text-base outline-0 ring-accent-500 placeholder:text-text-300 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 md:text-sm"
                            readOnly
                        />
                    </div>
                    <span>to</span>
                    <div>
                        <label htmlFor="maxValue" className="sr-only">Max Value</label>
                        <input
                            id="maxValue"
                            type="number"
                            value={maxValue}
                            className="focus-visible:ring-ring flex h-10 w-16 rounded-md border border-accent bg-primary-50 px-3 py-2 text-base outline-0 ring-accent-500 placeholder:text-text-300 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 md:text-sm"
                            readOnly
                        />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default MultiHandleSlider;
