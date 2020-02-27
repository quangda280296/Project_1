package quang.project2.view.playmusic;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ModelPagerAdapter extends FragmentStatePagerAdapter
{
  protected PagerModelManager pagerModelManager;

  public ModelPagerAdapter(FragmentManager paramFragmentManager, PagerModelManager paramPagerModelManager)
  {
    super(paramFragmentManager);
    this.pagerModelManager = paramPagerModelManager;
  }

  public int getCount()
  {
    return this.pagerModelManager.getFragmentCount();
  }

  public Fragment getItem(int paramInt)
  {
    return this.pagerModelManager.getItem(paramInt);
  }

  public CharSequence getPageTitle(int paramInt)
  {
    CharSequence localCharSequence;
    if (!this.pagerModelManager.hasTitles())
      localCharSequence = super.getPageTitle(paramInt);
    else
      localCharSequence = this.pagerModelManager.getTitle(paramInt);
    return localCharSequence;
  }
}

/* Location:           E:\Androidvn\jd-gui-0.3.3.windows\classes_dex2jar.jar
 * Qualified Name:     github.chenupt.multiplemodel.viewpager.ModelPagerAdapter
 * JD-Core Version:    0.6.0
 */