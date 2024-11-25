import UserPreferences from '@features/user/UserPreferences';
import UserAttributes from '@features/user/UserAttributes';

const SettingsPage = () => {
  return (
    <div className="mx-auto h-screen max-w-[800px] items-center justify-center overflow-auto bg-background-200 px-5 pt-24">
      <UserPreferences />
      <UserAttributes />
    </div>
  );
};

export default SettingsPage;
