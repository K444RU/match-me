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

    // Verify redirect to login page after registration
    await expect(page).toHaveURL('http://localhost:3000/login');

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
    await page.getByLabel('Gender').selectOption('1');

    await page.getByRole('button', { name: 'Pick a date' }).click();
    await page.getByLabel('Choose the Year').selectOption('1990');
    await page.getByLabel('Choose the Month').selectOption('6');
    await page.getByRole('button', { name: 'Tuesday, July 10th,' }).click();

    const cityInput = page.getByRole('textbox', { name: 'First name Last name Alias' });
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
    await page.getByLabel('Gender Preference').selectOption('2');
    await page.locator('#distance').fill('160');

    await page.getByRole('spinbutton', { name: 'Min Value' }).click();
    await page.getByRole('spinbutton', { name: 'Min Value' }).fill('25');
    await page.getByRole('spinbutton', { name: 'Max Value' }).click();
    await page.getByRole('spinbutton', { name: 'Max Value' }).fill('30');

    await expect(page.getByRole('heading', { name: 'Preferences' })).toBeVisible();
    await expect(page.locator('form')).toContainText('Preferences');

    await expect(page.getByRole('button', { name: 'Continue' })).toBeVisible();
    await page.getByRole('button', { name: 'Continue' }).click();

    await expect(page).toHaveURL('http://localhost:3000/chats');
  });
});
