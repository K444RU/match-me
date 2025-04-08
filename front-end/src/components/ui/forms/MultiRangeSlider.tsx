import MultiRangeSlider, { ChangeResult } from 'multi-range-slider-react';
import { Skeleton } from '../skeleton';

type MultiRangeSliderProps = {
  min: number;
  max: number;
  minValue: number | null;
  maxValue: number | null;
  label?: string;
  onInput: (e: ChangeResult) => void;
  showInputField?: boolean;
};

const MultiHandleSlider = ({
  min,
  max,
  minValue,
  maxValue,
  onInput,
  label,
  showInputField = true,
}: MultiRangeSliderProps) => {
  return (
    <div className="mb-3 w-full text-text">
      {label && <h2 className="pl-1 font-semibold">{label}</h2>}
      <div className="flex items-center gap-4">
        <div className="w-full">
          {minValue !== null && maxValue !== null ? (
            <MultiRangeSlider
              min={min}
              max={max}
              minValue={minValue}
              maxValue={maxValue}
              step={1}
              onInput={onInput}
              style={{ boxShadow: 'none' }}
              ruler={false}
              barLeftColor="white"
              barInnerColor="#e67d60"
              barRightColor="white"
              thumbLeftColor="#e67d60"
              thumbRightColor="#e67d60"
            />
          ) : (
            <div className="w-full">
              <Skeleton className="h-[58.8px] w-full rounded-[10px] border border-[gray]" />
            </div>
          )}
        </div>
        {showInputField && (
          <div className="flex items-center gap-4">
            <div>
              <label htmlFor="minValue" className="sr-only">
                Min Value
              </label>
              {minValue !== null && maxValue !== null ? (
                <input
                  id="minValue"
                  type="number"
                  value={minValue}
                  min={18}
                  max={maxValue - 1 || 119}
                  className="focus-visible:ring-ring flex h-10 w-16 rounded-md border border-accent bg-primary-50 px-3 py-2 text-base outline-0 ring-accent-500 placeholder:text-text-300 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 md:text-sm"
                />
              ) : (
                <span>loading</span>
              )}
            </div>
            <span>to</span>
            <div>
              <label htmlFor="maxValue" className="sr-only">
                Max Value
              </label>
              {minValue !== null && maxValue !== null ? (
                <input
                  id="maxValue"
                  type="number"
                  value={maxValue}
                  min={minValue + 1 || 19}
                  max={120}
                  className="focus-visible:ring-ring flex h-10 w-16 rounded-md border border-accent bg-primary-50 px-3 py-2 text-base outline-0 ring-accent-500 placeholder:text-text-300 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 md:text-sm"
                />
              ) : (
                <span>loading</span>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default MultiHandleSlider;
