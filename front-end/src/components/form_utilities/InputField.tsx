const InputField = ({ label }: { label: string }, { htmlFor }: { htmlFor: string }) => {
  return (
    <div className="mb-3 place-items-start">
      <label className="ml-2" htmlFor={`${htmlFor}`}>{label}</label>

    </div>
  )
}

export default InputField