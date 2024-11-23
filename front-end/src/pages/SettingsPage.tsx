import UserPreferences from '../components/ui/UserPreferences'
import UserAttributes from '../components/ui/UserAttributes'

const SettingsPage = () => {
  return (
    <div className="max-w-[800px] mx-auto pt-24 px-5 h-screen overflow-auto bg-background-200 items-center justify-center">
      <UserPreferences />
      <UserAttributes />
    </div>
  )
}

export default SettingsPage