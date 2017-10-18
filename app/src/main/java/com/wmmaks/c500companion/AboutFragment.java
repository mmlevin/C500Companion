/* C500Companion - An open source, free addition for Ownice C500 head unit
 * Copyright (C) 2017 Maksim M. Levin. Russia, Voronezh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contacts:
 *            email: mmlevin@mail.ru
*/

package com.wmmaks.c500companion;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.about,null);
        TextView textView = (TextView) v.findViewById(R.id.about_license);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        int defaultColor = textView.getTextColors().getDefaultColor();
        textView.setTextColor(defaultColor);
        textView = (TextView) v.findViewById(R.id.about_sources);
        textView.setTextColor(defaultColor);

        textView = (TextView)v.findViewById(R.id.about_version_name);
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            textView.setText(pInfo.versionName);
        } catch (Exception e) {
            //Log.e(LOG_TAG,e.getMessage());
        }

        return v;
    }
}
