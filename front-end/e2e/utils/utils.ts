export interface RandomValues {
  email: string;
  phone: string;
  alias: string;
  password: string;
  firstName: string;
  lastName: string;
  city: string;
}

export const getRandomValues = (): RandomValues => {
  const uniqueId = Date.now();
  const uniqueLetters = String.fromCharCode(65 + Math.floor(Math.random() * 26));
  return {
    email: `test-user-${uniqueId}${uniqueLetters}@example.com`,
    phone: `+3725555${Math.floor(Math.random() * 9000) + 1000}`,
    alias: `TestUser${uniqueLetters}`,
    password: 'password123',
    firstName: 'Test',
    lastName: 'User',
    city: 'Paide',
  };
};
