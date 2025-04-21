import { Button } from '@/components/ui/button';
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from '@/components/ui/command';
import { Input } from '@/components/ui/input';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { ScrollArea } from '@/components/ui/scroll-area';
import { cn } from '@/lib/utils';
import { useVirtualizer } from '@tanstack/react-virtual';
import { CheckIcon, ChevronsUpDown } from 'lucide-react';
import * as React from 'react';
import { useState } from 'react';
import { useRef } from 'react';
import * as RPNInput from 'react-phone-number-input';
import flags from 'react-phone-number-input/flags';

type PhoneInputProps = Omit<React.ComponentProps<'input'>, 'onChange' | 'value' | 'ref'> &
  Omit<RPNInput.Props<typeof RPNInput.default>, 'onChange'> & {
    onChange?: (value: RPNInput.Value) => void;
  };

const PhoneInput: React.ForwardRefExoticComponent<PhoneInputProps> = React.forwardRef<
  React.ElementRef<typeof RPNInput.default>,
  PhoneInputProps
>(({ className, onChange, ...props }, ref) => {
  return (
    <RPNInput.default
      ref={ref}
      className={cn('flex', className)}
      flagComponent={FlagComponent}
      countrySelectComponent={CountrySelect}
      inputComponent={InputComponent}
      smartCaret={false}
      /**
       * Handles the onChange event.
       *
       * react-phone-number-input might trigger the onChange event as undefined
       * when a valid phone number is not entered. To prevent this,
       * the value is coerced to an empty string.
       *
       * @param {E164Number | undefined} value - The entered value
       */
      onChange={(value) => onChange?.(value || ('' as RPNInput.Value))}
      {...props}
    />
  );
});
PhoneInput.displayName = 'PhoneInput';

const InputComponent = React.forwardRef<HTMLInputElement, React.ComponentProps<'input'>>(
  ({ className, ...props }, ref) => (
    <Input className={cn('rounded-e-lg rounded-s-none', className)} {...props} ref={ref} />
  )
);
InputComponent.displayName = 'InputComponent';

type CountryEntry = { label: string; value: RPNInput.Country | undefined };

type CountrySelectProps = {
  disabled?: boolean;
  value: RPNInput.Country;
  options: CountryEntry[];
  onChange: (country: RPNInput.Country) => void;
};

const CountrySelect = ({ disabled, value: selectedCountry, options: countryList, onChange }: CountrySelectProps) => {
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [isOpen, setIsOpen] = useState(false);
  const parentRef = useRef<HTMLDivElement>(null);

  const validCountries = React.useMemo(() => {
    return countryList.filter((country): country is Required<CountryEntry> => Boolean(country.value));
  }, [countryList]);

  const filteredCountries = React.useMemo(() => {
    if (!searchTerm) return validCountries;
    return validCountries.filter(
      (country) =>
        country.label.toLowerCase().includes(searchTerm.toLowerCase()) ||
        `+${RPNInput.getCountryCallingCode(country.value!!)}`.includes(searchTerm)
    );
  }, [validCountries, searchTerm]);

  React.useEffect(() => {
    if (!isOpen) {
      setSearchTerm('');
    }
  }, [isOpen]);

  const rowVirtualizer = useVirtualizer({
    count: filteredCountries.length,
    getScrollElement: () => parentRef.current,
    estimateSize: React.useCallback(() => 36, []),
    overscan: 5,
    enabled: isOpen,
    debug: true,
  });

  return (
    <Popover open={isOpen} onOpenChange={setIsOpen}>
      <PopoverTrigger asChild>
        <Button
          type="button"
          variant="outline"
          className="flex gap-1 rounded-e-none rounded-s-lg border-r-0 px-3 focus:z-10"
          disabled={disabled}
          onClick={() => setIsOpen(!isOpen)} // Usually not needed with onOpenChange
        >
          <FlagComponent country={selectedCountry} countryName={selectedCountry} />
          <ChevronsUpDown className={cn('-mr-2 size-4 opacity-50', disabled ? 'hidden' : 'opacity-100')} />
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-[400px] p-0 shadow-lg" align="start" sideOffset={4}>
        {/* Search Input stays outside the scroll container */}
        <Input
          // Simple Input, not CommandInput
          className="sticky top-0 z-10 border-b p-2 px-4 rounded-t-md rounded-b-none"
          onChange={(e) => setSearchTerm(e.target.value)}
          value={searchTerm}
          placeholder="Search country..."
        />

        {/* This div becomes the scroll container */}
        <div
          ref={parentRef} // 1. Attach the ref here
          style={{
            height: `288px`, // 2. Set height (e.g., 8 items * 36px estimate) - Adjust as needed!
            width: '100%',
            overflow: 'auto', // 3. Enable scrolling
            position: 'relative',
          }}
        >
          {/* This div handles the total size */}
          <div
            style={{
              height: `${rowVirtualizer.getTotalSize()}px`,
              width: '100%',
              position: 'relative',
            }}
          >
            {/* Map over virtual items */}
            {rowVirtualizer.getVirtualItems().map((virtualRow) => {
              // Get data from the FILTERED list
              const countryEntry = filteredCountries[virtualRow.index];
              // Important: Check if countryEntry and countryEntry.value exist
              if (!countryEntry || !countryEntry.value) {
                console.warn('Virtualized item missing data at index:', virtualRow.index);
                return null;
              }
              const { value: country, label: countryName } = countryEntry;

              return (
                <div
                  key={virtualRow.key}
                  data-index={virtualRow.index}
                  ref={rowVirtualizer.measureElement}
                  style={{
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    width: '100%',
                    height: `${virtualRow.size}px`,
                    transform: `translateY(${virtualRow.start}px)`,
                    padding: '0.5rem',
                    display: 'flex', // To align items like CommandItem
                    alignItems: 'center', // To align items like CommandItem
                  }}
                  // Make the div clickable like CommandItem
                  onClick={() => {
                    console.log('TRIED TO CHANGE COUNTRY');
                    onChange(country);
                    setIsOpen(false);
                  }}
                  className="hover:bg-secondary/40 select-none"
                >
                  <CountrySelectOption country={country} countryName={countryName} selectedCountry={selectedCountry} />
                </div>
              );
            })}
          </div>
        </div>
        {/* Optional: Show message if filtered list is empty */}
        {filteredCountries.length === 0 && (
          <div className="p-4 text-center text-sm text-muted-foreground">No country found.</div>
        )}
      </PopoverContent>
    </Popover>
  );
};

interface CountrySelectOptionProps extends RPNInput.FlagProps {
  selectedCountry: RPNInput.Country;
}

const CountrySelectOption = ({ country, countryName, selectedCountry }: CountrySelectOptionProps) => {
  return (
    <>
      <FlagComponent country={country} countryName={countryName} />
      <span className="flex-1 text-sm">{countryName}</span>
      <span className="text-foreground/50 text-sm">{`+${RPNInput.getCountryCallingCode(country)}`}</span>
      <CheckIcon className={`ml-auto size-4 ${country === selectedCountry ? 'opacity-100' : 'opacity-0'}`} />
    </>
  );
};

const FlagComponent = ({ country, countryName }: RPNInput.FlagProps) => {
  const Flag = flags[country];

  return (
    <span className="flex h-4 w-9 overflow-hidden rounded-xs [&_svg]:size-full">
      {Flag && (
        <div className="scale-120 mx-auto">
          <Flag title={countryName} />
        </div>
      )}
    </span>
  );
};

export { PhoneInput };
