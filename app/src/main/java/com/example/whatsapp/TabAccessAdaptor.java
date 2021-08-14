package com.example.whatsapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabAccessAdaptor extends FragmentPagerAdapter {

    public TabAccessAdaptor(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i)
    {
        switch (i)
        {
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return  chatsFragment;


            case 1:
                GroupFragment groupFragment = new GroupFragment();
                return  groupFragment;


            case 2:
              ContactsFragment contactsFragment = new ContactsFragment();
                return  contactsFragment;

                default:
                    return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position)
        {
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
               return "Chats";


            case 1:
                GroupFragment groupFragment = new GroupFragment();
                return  "Groups";


            case 2:
                ContactsFragment contactsFragment = new ContactsFragment();
                return  "Contacts";

            default:
                return null;
        }

    }
}
