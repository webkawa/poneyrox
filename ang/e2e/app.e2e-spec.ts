import { ModsPage } from './app.po';

describe('mods App', () => {
  let page: ModsPage;

  beforeEach(() => {
    page = new ModsPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
