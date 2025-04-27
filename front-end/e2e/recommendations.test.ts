import { expect, test } from '@playwright/test';
import { getRandomValues } from 'utils/utils';

test.describe('Recommendations', () => {
  const testUser = getRandomValues();
  const RECOMMENDATIONS_API_URL = 'http://localhost:8000/connections/recommendations';
  const AUTH_TOKEN_STORAGE_KEY = 'blind-auth-token';

  test('user is not shown any recommendations until they have completed their profile', async ({ page }) => {
    await page.goto('http://localhost:3000/');

    // register
    await page.getByRole('link', { name: 'Sign Up' }).click();
    await page.getByRole('textbox', { name: 'Email' }).click();
    await page.getByRole('textbox', { name: 'Email' }).fill(testUser.email);
    await page.getByRole('textbox', { name: 'Password' }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill(testUser.password);
    await page.getByRole('textbox', { name: 'Enter a phone number' }).click();
    await page.getByRole('textbox', { name: 'Enter a phone number' }).fill(testUser.phone);
    await page.getByRole('button', { name: 'Submit form.' }).click();

    await page.goto('http://localhost:3000/login');

    // login
    await page.getByRole('textbox', { name: 'Email' }).click();
    await page.getByRole('textbox', { name: 'Email' }).fill(testUser.email);
    await page.getByRole('textbox', { name: 'Password' }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill(testUser.password);
    await page.getByRole('button', { name: 'Submit form.' }).click();

    // shouldnt allow
    await page.getByRole('link', { name: 'Chats' }).click();

    // should be the same
    await expect(page).toHaveURL('http://localhost:3000/profile-completion');

    const authToken = await page.evaluate(() => localStorage.getItem(AUTH_TOKEN_STORAGE_KEY));
    expect(authToken).toBeTruthy();

    const response = await fetch(RECOMMENDATIONS_API_URL, {
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    expect(response.status).toBe(404);
  });
});
