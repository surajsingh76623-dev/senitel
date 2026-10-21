import '@testing-library/jest-dom';

// Polyfill window.fetch for testing if needed
if (!global.fetch) {
  global.fetch = (() =>
    Promise.resolve({
      ok: true,
      json: () => Promise.resolve([]),
    })) as any;
}
