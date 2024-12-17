import { Toaster } from '@/components/ui/sonner';
import UnifiedForm from './components/UnifiedForm';

const ProfileCompletionPage = () => {
  // gender | option to keep private | Woman, Man, More -> gender.ts
  // sexual-orientation/sexual-identity | option to keep private -> sexuality.ts
  // Integer age 18-100+;

  // private String location;
  // private Integer score;

  // prefs
  // Gender gender; who are you interested in seeing women | men | everyone
  // Integer age_min;
  // Integer age_max;
  // Integer distance;

  return (
    <div className="mx-auto flex h-screen max-w-7xl items-center justify-center">
      <UnifiedForm />
      <Toaster className='bg-black text-white'/>
    </div>
  );
};

export default ProfileCompletionPage;
