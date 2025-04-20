import { genders } from '@/assets/genders';
import { FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Control } from 'react-hook-form';

interface GenderSelectProps {
  control: Control<any>;
  name: string;
  label?: string;
  description?: string;
}

export default function GenderSelect({
  control,
  name,
  label = 'Gender',
  description = 'What gender are you?',
}: GenderSelectProps) {
  return (
    <FormField
      control={control}
      name={name}
      render={({ field }) => (
          <FormItem className="w-full">
            <FormLabel htmlFor={name}>{label}</FormLabel>
            <Select
              value={field.value ?? ''}
              onValueChange={(value) => {
                if (value) {
                  field.onChange(value);
                }
              }}
            >
              <FormControl>
                <SelectTrigger id={name} className="w-full">
                  <SelectValue placeholder="Select a gender..." />
                </SelectTrigger>
              </FormControl>
              <SelectContent position="popper">
                {genders.map((gender) => (
                  <SelectItem key={gender.value} value={gender.value}>
                    {gender.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            {description && <FormDescription>{description}</FormDescription>}
            <FormMessage />
          </FormItem>
        )}
    />
  );
}
