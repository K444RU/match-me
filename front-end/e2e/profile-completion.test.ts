import { expect, test } from '@playwright/test';
import { getRandomValues } from 'utils/utils';

test.describe('Full User Registration Flow', () => {
  const testUser = getRandomValues();
  const mockCity = {
    name: 'Paide',
    latitude: 58.8869377,
    longitude: 25.5699277,
    country: 'EE',
  };

  test.beforeEach(async ({ page }) => {
    await page.route('https://api.api-ninjas.com/v1/geocoding**', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([mockCity]),
      });
    });
  });

  test('should complete full registration and profile completion flow', async ({ page }) => {
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

    // Verify not redirected to login page
    await expect(page).toHaveURL('http://localhost:3000/register');

    await page.getByRole('link', { name: 'Log in' }).click();

    await page.waitForTimeout(2000);

    // login
    await page.getByRole('textbox', { name: 'Email' }).click();
    await page.getByRole('textbox', { name: 'Email' }).fill(testUser.email);
    await page.getByRole('textbox', { name: 'Password' }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill(testUser.password);
    await page.getByRole('button', { name: 'Submit form.' }).click();

    // profile completion
    await expect(page).toHaveURL('http://localhost:3000/profile-completion');
    await expect(page.getByRole('heading', { name: 'Personal Information' })).toBeVisible();

    await page.getByRole('textbox', { name: 'Michael' }).click();
    await page.getByRole('textbox', { name: 'Michael' }).fill(testUser.firstName);
    await page.getByRole('textbox', { name: 'Doorstep' }).click();
    await page.getByRole('textbox', { name: 'Doorstep' }).fill(testUser.lastName);
    await page.getByRole('textbox', { name: 'Shotgunner404' }).click();
    await page.getByRole('textbox', { name: 'Shotgunner404' }).fill(testUser.alias);

    await page.getByPlaceholder('Select your hobbies...').click();
    await page.getByRole('option', { name: 'Animation' }).click();
    await page.getByRole('option', { name: 'Baton twirling' }).click();
    await page.getByText('Hobbies').click();

    const genderDropdown = page.getByRole('combobox').filter({ hasText: 'Select Gender' });
    await genderDropdown.click();

    const maleOption = page.getByRole('option').filter({ has: page.getByText('Male', { exact: true }) });
    await maleOption.waitFor({ state: 'visible', timeout: 5000 });
    await maleOption.click();

    await page.getByRole('button', { name: 'Pick a date' }).click();
    await page.getByRole('button', { name: 'April 2025' }).click();
    await page.getByRole('button', { name: 'Go to the previous 12 years' }).click();
    await page.getByRole('button', { name: 'Go to the previous 12 years' }).click();
    await page.getByRole('button', { name: '1996', exact: true }).click();
    await page.getByRole('button', { name: 'Monday, January 8th,' }).click();
    await page.getByLabel('', { exact: true }).press('Escape');

    const cityInput = page.getByRole('textbox', { name: 'Enter your city' });
    await cityInput.click();

    // Start waiting for the response BEFORE filling the input
    const responsePromise = page.waitForResponse('https://api.api-ninjas.com/v1/geocoding**', { timeout: 5000 });

    await cityInput.fill('Paide');

    await responsePromise;

    const suggestionListItem = page
      .getByRole('listitem')
      .filter({ has: page.locator('span.font-medium', { hasText: 'Paide' }) });

    await suggestionListItem.waitFor({ state: 'visible', timeout: 10000 });

    await suggestionListItem.click({ timeout: 10000 });

    await page.getByRole('button', { name: 'Continue' }).click();

    const genderDropdown2 = page.getByRole('combobox').filter({ hasText: 'Select Gender' });
    await genderDropdown2.click();

    const femaleOption = page.getByRole('option').filter({ has: page.getByText('Female', { exact: true }) });
    await femaleOption.waitFor({ state: 'visible', timeout: 5000 });
    await femaleOption.click();

    await expect(page.getByRole('heading', { name: 'Preferences' })).toBeVisible();

    await expect(page.getByRole('button', { name: 'Continue' })).toBeVisible();
    await page.getByRole('button', { name: 'Continue' }).click();

    await expect(page).toHaveURL('http://localhost:3000/chats');
  });
});
