import InputField from '@/components/ui/forms/InputField';
import { Dispatch, SetStateAction} from 'react';

interface PhoneInputProps {
  countryCode: string;
  phone: string;
  onCountryCodeChange: Dispatch<SetStateAction<string>>;
  onPhoneChange: Dispatch<SetStateAction<string>>;
}

const PhoneInput = ({
  countryCode,
  phone,
  onCountryCodeChange,
  onPhoneChange,
}: PhoneInputProps) => {
  return (
    <>
      <InputField
        className="w-24"
        type="text"
        name="country_code"
        placeholder="+372"
        value={countryCode}
        onChange={onCountryCodeChange}
        required={true}
      />
      <InputField
        type="text"
        name="contact_phone"
        placeholder="Phone"
        value={phone}
        onChange={onPhoneChange}
        required={true}
      />
    </>
  );
};

export default PhoneInput;
