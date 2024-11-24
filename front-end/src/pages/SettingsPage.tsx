import UserPreferences from '@features/user/UserPreferences';
import UserAttributes from '@features/user/UserAttributes';

const SettingsPage = () => {
  return (
    <div className="max-w-[800px] mx-auto pt-24 px-5 h-screen overflow-auto bg-background-200 items-center justify-center">
      <UserPreferences />
      <UserAttributes />
    </div>
  )
}

export default SettingsPage